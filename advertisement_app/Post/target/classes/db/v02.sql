CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    creator_id UUID NOT NULL ,
    thumbnail_url TEXT NOT NULL,
    video_url TEXT NOT NULL,
    image_urls TEXT[] NOT NULL,                       -- array of URLs
    boosted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL ,
    updated_at TIMESTAMPTZ NOT NULL ,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content_type VARCHAR(50) NOT NULL,       -- e.g. video, image, text
    is_visible BOOLEAN NOT NULL ,
    is_active BOOLEAN NOT NULL
);
