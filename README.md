<<<<<<< silahubv05_development
# 🗄️ SILAHUB — Data Reset & Post Insertion Workflow

---

## ⚠️ CRITICAL WARNING

> **ONLY follow the "Data Reset" section if you have CORRUPTED DATA.**
> Otherwise, skip directly to → **[Initialize Data](#-initialize-data--postman-setup)**

---

## 🔴 STEP 0 — Data Reset (CORRUPTED DATA ONLY)

### MongoDB — Delete Collections

1. Open MongoDB Compass or your MongoDB client
2. Find and **delete** the collection: `feeds`
3. Find and **delete** the collection: `post_interaction_stats`

---

### Redis — Remove All Keys

Open CMD and run:

```bash
redis-cli
```

Then delete each key manually:

```bash
DEL <keyName>
```

> Replace `<keyName>` with the actual key name you want to remove. Repeat for each key.

---

### PostgreSQL — Truncate Tables

Run the following SQL commands **in order**:

```sql
TRUNCATE post_likes;
TRUNCATE post_upvotes;
DELETE FROM post_interactions;
TRUNCATE post_assets;
DELETE FROM posts;
```

---

---

## 🟢 Initialize Data — Postman Setup

### Create Postman Collection

Create a new collection in Postman named: **`SILAHUB_V05`**

---

## 📌 API 1 — Create Post Intent

> **Purpose:** Creates a post intent. Required before creating a post.
> The video upload is **required** to appear in the feed. Asset upload is **optional**.

**Method:** `POST`
**URL:** `http://localhost:8080/api/post/v0/post_intent`

**Headers:**

| Key | Value |
|---|---|
| Authorization | `cfd8a81c-7bae-4f3b-a8f2-f1e280e51b43` |
| Content-Type | `application/json` |

**Body (JSON):**

```json
{
  "image_content_types": [
    "image/jpeg",
    "image/jpeg"
  ],
  "thumbnail_content_type": "image/jpeg",
  "title": "Second Post To test asset and video upload to feed",
  "description": "This is a detailed description of my post that can be up to 2000 characters long.",
  "is_visible": true
}
```

> 💡 After a successful response, copy the **presigned video upload URL** and the **video ID** from the response — you'll need them in the next step.

---

## 📌 API 2 — Upload Video to S3

> **Purpose:** Uploads the video file to Amazon S3 using the presigned URL from the previous step.

**Method:** `PUT`
**URL:** *(paste the presigned URL from Step 1 here)*

```
https://amzn-s3-bucket-lb-01.s3.eu-north-1.amazonaws.com/posts/raw/<VIDEO_ID>_v.mp4?X-Amz-Algorithm=<ALGORITHM>&X-Amz-Date=<DATE>&X-Amz-SignedHeaders=host&X-Amz-Credential=<CREDENTIAL>&X-Amz-Expires=<EXPIRY>&X-Amz-Signature=<SIGNATURE>
```

| Variable | Description |
|---|---|
| `<VIDEO_ID>` | The unique video ID returned from the Post Intent response |
| `<ALGORITHM>` | Signing algorithm (e.g. `AWS4-HMAC-SHA256`) |
| `<DATE>` | Signing date/time (e.g. `20260128T092955Z`) |
| `<CREDENTIAL>` | Your AWS credential string |
| `<EXPIRY>` | URL expiry in seconds (e.g. `3600`) |
| `<SIGNATURE>` | The computed HMAC signature |

**Headers:**

| Key | Value |
|---|---|
| Content-Type | `video/mp4` |

**Body:**
- Select **Binary**
- Choose your `.mp4` video file

---

### ✅ After Receiving HTTP 200 — Verify All Systems

Check each system to confirm the data was saved correctly:

**Amazon S3:**
- Go to your S3 console
- Navigate to `posts > hls > <VIDEO_ID>/`
- Confirm the new video folder exists

**MongoDB:**
- Check `feeds` collection → a new document should be created
- Check `post_interaction_stats` → a new document should be created

**Redis:**
- Verify a new value is inserted in one of the score buckets

**PostgreSQL:**
- `posts` → new row with `status = active`
- `post_assets` → new row linked to this post
- `post_interactions` → new row linked to this post

---

## 📌 API 3 — Upload Asset Images to S3

> **Purpose:** Uploads image assets associated with the post.

**Method:** `PUT`
**URL:** *(paste the presigned asset URL from Step 1)*

```
https://amzn-s3-bucket-lb-01.s3.eu-north-1.amazonaws.com/posts/assets/origin/images/<ASSET_ID>.jpg?X-Amz-Algorithm=<ALGORITHM>&X-Amz-Date=<DATE>&X-Amz-SignedHeaders=host&X-Amz-Credential=<CREDENTIAL>&X-Amz-Expires=<EXPIRY>&X-Amz-Signature=<SIGNATURE>
```

| Variable | Description |
|---|---|
| `<ASSET_ID>` | The unique asset ID from the Post Intent response |

**Headers:**

| Key | Value |
|---|---|
| Content-Type | `image/png` |

**Body:**
- Select **Binary**
- Choose your image file (must match the declared content type)

---

### ✅ After Uploading Asset — Verify

- **S3 Console:** `posts > assets > images` → confirm image is uploaded
- **PostgreSQL `post_assets`:** confirm the URI is saved with `status = active`

---

### ⚙️ Manual Step — Set Video URL

> For now, the `video_url` must be set manually in PostgreSQL.

1. Go to PostgreSQL → `posts` table
2. Copy the S3 video URI for the post
3. Share it with the AI to generate the correctly formatted video URL
4. Update the `posts` row with the generated URL

---

---

## 🔁 STEP — Repeat x9 (Scrolling Test)

Repeat the full flow above **9 times** to generate enough posts to test infinite scroll on the frontend.

> ⚠️ **Do NOT delete the `posts` folder in Amazon S3** — it must stay intact for the export below.

---

---

## 💾 Export & Save Data (After 9 Posts)

Save all data so it can be **reused later** without re-uploading.

### MongoDB Export

Export the following collections as `.json`:
- `feeds`
- `post_interaction_stats`

Use MongoDB Compass → right-click collection → **Export Collection → JSON**

---

### PostgreSQL Export

Export the following tables:
- `posts`
- `post_interactions`
- `post_assets`

Use `pg_dump`, DBeaver, or your preferred client to export as SQL or CSV.

---

### Redis Export

> Redis does not support native export. Instead, manually record each `postId` with a score value.

Create a JSON file in this format:

```json
[
  { "postId": "d945527d-8da4-4adc-857f-61eb929952c8", "score": 142 },
  { "postId": "0335bfa4-ca7c-4fb4-899b-f55d56e400cc", "score": 98 },
  ...
]
```

> Assign a **random score** to each post (e.g. between 50–500). These will be used to re-seed Redis when needed.

---

*Last updated: March 2026 — SILAHUB v0.5*
=======
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
>>>>>>> main
