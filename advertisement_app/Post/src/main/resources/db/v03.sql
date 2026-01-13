-- ======================================
-- Posts Table
-- ======================================
CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    creator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content_type VARCHAR(50) NOT NULL,
    video_uri TEXT NOT NULL,
    is_visible BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ======================================
-- PostAsset Table (Thumbnail + Images)
-- ======================================
CREATE TABLE post_asset (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL, -- 'thumbnail' or 'image'
    uri TEXT NOT NULL
);

-- ======================================
-- JobVideo Table
-- ======================================
CREATE TABLE job_video (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_s3_key TEXT NOT NULL,
    status VARCHAR(50) NOT NULL
);