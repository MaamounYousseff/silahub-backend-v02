CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    creator_id UUID NOT NULL,

    image_count INTEGER,
    thumbnail_count INTEGER,

    is_visible BOOLEAN NOT NULL,

    object_s3_key_prefix VARCHAR NOT NULL,
    object_s3_key_suffix VARCHAR,

    status VARCHAR NOT NULL DEFAULT 'pending',

    title VARCHAR NOT NULL,
    description TEXT,

    s3_video_uri TEXT NOT NULL DEFAULT '',

    CONSTRAINT posts_creator_id_fkey
        FOREIGN KEY (creator_id) REFERENCES users(id),

    CONSTRAINT posts_image_count_check
        CHECK (image_count IS NULL OR image_count >= 0),

    CONSTRAINT posts_thumbnail_count_check
        CHECK (thumbnail_count IS NULL OR thumbnail_count >= 0)
);



CREATE TABLE post_interactions (
    post_id UUID PRIMARY KEY,

    total_views BIGINT NOT NULL,
    total_upvotes BIGINT NOT NULL,
    total_likes BIGINT NOT NULL,
    total_watch_seconds BIGINT NOT NULL,
    total_clicks BIGINT NOT NULL,

    boosted_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT post_interactions_post_id_fkey
        FOREIGN KEY (post_id) REFERENCES posts(id)
);


CREATE TABLE post_assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    post_id UUID NOT NULL,

    type VARCHAR NOT NULL,
    status VARCHAR NOT NULL DEFAULT 'pending',

    s3_asset_prefix VARCHAR NOT NULL,
    s3_asset_suffix VARCHAR,
    s3_asset_uri VARCHAR,

    CONSTRAINT post_assets_post_id_fkey
        FOREIGN KEY (post_id) REFERENCES posts(id)
);


