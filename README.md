
SILAHUB VERSION 5 -- 

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
DEL 
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
