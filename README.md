# SilaHub — Developer Setup & Integration Guide

---

## Table of Contents

- [01 · Prerequisites](#01--prerequisites)
- [02 · Database Setup](#02--database-setup)
- [03 · AWS Configuration](#03--aws-configuration)
- [04 · System Architecture](#04--system-architecture)
- [05 · Modules Overview](#05--modules-overview)
- [06 · Postman Collection](#06--postman-collection)
- [07 · Testing Flow](#07--testing-flow)

---

## 01 · Prerequisites

| Dependency   | Version            |
|--------------|--------------------|
| PostgreSQL   | `17.x`             |
| Redis        | `5.0.14.1`         |
| Java         | `21 (LTS)`         |
| MongoDB      | `Latest stable`    |
| Postman      | `Any recent`       |

---

## 02 · Database Setup

### 2.1 Create the Database

Create a fresh PostgreSQL database named `silahub_v04`.

### 2.2 Restore the Schema

Restore the provided SQL dump into the newly created database using `script.sql`.

### 2.3 Truncate Required Tables

After restoring, clear the following tables to start from a clean state. Run in this order to respect foreign key constraints:

```sql
TRUNCATE TABLE post_interactions, post_likes, post_upvotes, post_assets, posts CASCADE;
```

> **Note:** `CASCADE` handles dependent rows automatically. Double-check your FK constraints before running in a shared environment.

---

## 03 · AWS Configuration

The service relies on S3 for file storage and SQS for event-driven processing. You will need an AWS account with permissions to create and configure these resources.

### 3.1 S3 Bucket

Create an S3 bucket with the following exact name:

```
amzn-s3-bucket-lb-01
```

| Setting        | Value                                                                              |
|----------------|------------------------------------------------------------------------------------|
| Region         | `eu-north-1` (Stockholm) — keep consistent across all AWS resources                |
| Public access  | Block all public access                                                            |
| Versioning     | Optional, recommended for production                                               |
| CORS           | Allow `PUT` requests from your application domain (required for pre-signed URL uploads) |

The bucket is used for:

- Raw video uploads → `posts/raw/`
- Processed video assets → `posts/assets/`
- Image assets → `posts/assets/origin/images/`

---

### 3.2 SQS Queue: `post_service_asset_upload`

Create a **Standard** SQS queue named `post_service_asset_upload`. This queue handles asset upload notifications triggered by SNS topics.

After creating the queue, open the **Access Policy** tab and apply the following policy. Replace `<YOUR_ACCOUNT_ID>` and `<YOUR_REGION>` with your own values:

```json
{
  "Version": "2012-10-17",
  "Id": "__default_policy_ID",
  "Statement": [
    {
      "Sid": "__owner_statement",
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::<YOUR_ACCOUNT_ID>:root"
      },
      "Action": "SQS:*",
      "Resource": "arn:aws:sqs:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:post_service_asset_upload"
    },
    {
      "Sid": "topic-subscription-asset_upload.fifo",
      "Effect": "Allow",
      "Principal": { "Service": "sns.amazonaws.com" },
      "Action": "SQS:SendMessage",
      "Resource": "arn:aws:sqs:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:post_service_asset_upload",
      "Condition": {
        "ArnLike": {
          "aws:SourceArn": "arn:aws:sns:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:asset_upload.fifo"
        }
      }
    },
    {
      "Sid": "topic-subscription-asset_upload",
      "Effect": "Allow",
      "Principal": { "Service": "sns.amazonaws.com" },
      "Action": "SQS:SendMessage",
      "Resource": "arn:aws:sqs:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:post_service_asset_upload",
      "Condition": {
        "ArnLike": {
          "aws:SourceArn": "arn:aws:sns:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:asset_upload"
        }
      }
    }
  ]
}
```

---

### 3.3 SQS Queue: `transcoding-queue`

Create a second **Standard** SQS queue named `transcoding-queue`. This receives messages from both S3 bucket events and SNS video upload topics.

**Queue Settings:**

| Setting                    | Value                    |
|----------------------------|--------------------------|
| Visibility Timeout         | `3 minutes (180s)`       |
| Message Retention Period   | `30 minutes (1800s)`     |
| Delivery Delay             | `0 seconds`              |
| Receive Message Wait Time  | `0 seconds`              |
| Encryption                 | Disabled                 |

**Access Policy:**

```json
{
  "Version": "2012-10-17",
  "Id": "__default_policy_ID",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": { "Service": "s3.amazonaws.com" },
      "Action": "sqs:SendMessage",
      "Resource": "arn:aws:sqs:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:transcoding-queue",
      "Condition": {
        "ArnLike": { "aws:SourceArn": "arn:aws:s3:::amzn-s3-bucket-lb-01" }
      }
    },
    {
      "Sid": "topic-subscription-video_uploaded",
      "Effect": "Allow",
      "Principal": { "Service": "sns.amazonaws.com" },
      "Action": "SQS:SendMessage",
      "Resource": "arn:aws:sqs:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:transcoding-queue",
      "Condition": {
        "ArnLike": {
          "aws:SourceArn": "arn:aws:sns:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:video_uploaded"
        }
      }
    },
    {
      "Sid": "topic-subscription-video_upload",
      "Effect": "Allow",
      "Principal": { "Service": "sns.amazonaws.com" },
      "Action": "SQS:SendMessage",
      "Resource": "arn:aws:sqs:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:transcoding-queue",
      "Condition": {
        "ArnLike": {
          "aws:SourceArn": "arn:aws:sns:<YOUR_REGION>:<YOUR_ACCOUNT_ID>:video_upload"
        }
      }
    }
  ]
}
```

> **Note:** The S3 permission allows `amzn-s3-bucket-lb-01` to push messages directly into this queue when object events fire. Make sure the S3 bucket notification is also configured on the bucket side to point at this queue ARN.

---

## 04 · System Architecture

> 📎 See `system_design.png` and `system_architecture.png` for the visual references.

Each module in SilaHub follows the same internal structure. As shown in `system_architecture.png`, every module is divided into four concentric layers:

- **Web / API** — outermost layer; REST controllers and inter-module contracts
- **Logic** — service layer
- **Domain** — core business entities and rules
- **Infrastructure** — innermost layer; repos, event handlers, and external adapters

In practice this means a REST call comes in through the Web layer, gets processed by the Logic (service), works with Domain objects, and any persistence or messaging happens in Infrastructure — nothing leaks across layers in the other direction.

---

## 05 · Modules Overview

> 📎 See `modules_path.png` for the full project tree.

The project is a multi-module Maven application (`pom.xml` at the root). The modules are:

---

### `App`

The entry point. Starts the Spring Boot application and pulls in all other modules.

---

### `Feed`

**Actors:** Explorer  
**Stores:** MongoDB · Redis `feed:history:<user-id>`

The Feed module is responsible for two things: serving the feed to an explorer, and making sure the same video is never shown to the same user twice. The watch history per user is kept in Redis under `feed:history:<user-id>` so lookups stay fast. The MongoDB feed collection holds the post data that gets surfaced to the explorer.

---

### `Interaction`

**Actors:** Explorer, Promoter  
**Stores:** `post_interactions` · `post_likes` · `post_upvotes`

Handles every interaction a user can have with a post: like, upvote, watch time, and click. Inside the module, the Domain layer owns the repo and aggregation logic, the Application layer handles validation, calls the service, and publishes events, and the Infrastructure layer contains the NoSQL adapter and the producer that pushes to Kafka.

---

### `Post`

**Actor:** Creator  
**Stores:** `posts` · `post_assets`

Manages post creation from the creator's side. When a creator starts a post, this module stores the intent in `posts` and `post_assets` and coordinates with S3 to generate the pre-signed URLs the client needs to upload the video and images directly.

---

### `Scoring`

**Stores:** Redis ZSets: `bucket1` · `bucket2` · `bucket3`

Uses Redis ZSets to maintain post scores across three buckets. Like and watch time scores are batched — the score only recalculates once a threshold of events is reached, and when a post crosses a bucket boundary it moves to the appropriate ZSet. Upvote score is updated immediately on every toggle, no batching.

---

### `MediaIngestion`

Handles the media ingestion pipeline. Listens to the transcoding queue and processes video and asset files after they land in S3.

---

### `Shared`

Holds the common code used across all modules: DTOs, response wrappers (`SilahubResponse`, `SilahubResponseUtil`), and shared utilities.

---

### `SharedModuleTest`

Not a production module. When running a single module in isolation during development, Spring cannot inject beans that live in another module. `SharedModuleTest` re-declares those beans so isolated module tests can run without loading the entire application context.

---

### `UserAdmin` *(WIP)*

Not yet complete. Currently implements a paginated view of registered users following a standard MVC structure. Not connected to the main interaction flow yet.

---

## 06 · Postman Collection

### 6.1 Import

1. Open Postman
2. Click **File > Import**
3. Select `SilaHub_v05.postman_collection`
4. Confirm — you should see three folders: `interaction`, `post`, and `feed`

### 6.2 Configure Environment

Set up a Postman environment (or use collection variables) with at least:

| Variable     | Example Value           |
|--------------|-------------------------|
| `base_url`   | `http://localhost:8080` |
| `protocol`   | `http`                  |
| `host`       | `localhost`             |
| `port`       | `8080`                  |

---

## 07 · Testing Flow

### 7.1 Publishing a Post

Follow these three steps in order. Each depends on the output of the previous one.

**Step 1 — Create Post Intent**  
Folder: `post > Create Post Intent`  
`POST /api/post/v0/post_intent`

Registers a new post and returns pre-signed S3 URLs for uploading the video and image assets. Copy those URLs — you will need them in steps 2 and 3.

**Step 2 — Upload Video to S3**  
Folder: `post > Upload Video To S3`  
`PUT <pre-signed URL from step 1>`

Set the request body to **binary / file** and select your `.mp4`. No `Authorization` header is needed — the pre-signed URL carries the credentials.

**Step 3 — Upload Image Assets to S3**  
Folder: `post > Upload Assets to S3`  
`PUT <pre-signed URL from step 1>`

Same approach as the video upload, but select your image file. Repeat for each image URL returned in the intent response.

---

### 7.2 Interactions

Folder: `interaction`

Once you have at least one post in the feed, use these endpoints to simulate user interactions. All three require the `feedPostId` path variable.

| Request        | Endpoint                                                  |
|----------------|-----------------------------------------------------------|
| `toggleLike`   | `POST /api/interactions/v0/like/{feedPostId}`             |
| `toggleUpvote` | `POST /api/interactions/v0/upvote/{feedPostId}`           |
| `watchTime`    | `POST /api/interactions/v0/watch/{feedPostId}?watchTime={ms}` |

**Scoring behavior:**

- **Like & Watch Time** — score recalculates after hitting a batch threshold. Once the threshold is reached, the post score updates and the post may move between Redis buckets.
- **Upvote** — score updates instantly on every toggle.

> 💡 Keep an eye on your service logs when triggering likes and watch time in bulk — you will see the score recalculation fire once the threshold is hit.

---

### 7.3 Feed

Folder: `feed`

| Request              | Endpoint                                                              |
|----------------------|-----------------------------------------------------------------------|
| `get feed`           | `GET /api/v0/feed` — paginated feed, offset tracked via cookie        |
| `feed post details`  | `GET /api/v0/feed/feedDetails?postId={uuid}`                          |

---

*SilaHub — Internal Developer Guide — v0.5*
