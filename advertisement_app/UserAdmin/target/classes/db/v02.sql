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


CREATE TABLE post_interactions (
    post_id UUID PRIMARY KEY REFERENCES posts(id) ON DELETE CASCADE,
    total_views BIGINT NOT NULL ,
    total_upvotes BIGINT NOT NULL ,
    total_likes BIGINT NOT NULL ,
    total_watch_seconds BIGINT NOT NULL ,
    total_clicks BIGINT NOT NULL 
);


-- SHOULD DELETED IF USER OR POST DELETE -- BUT I DO NOT IMPLEMENTED BECAUSE OF UN-KNOWN FUTURE PERFORMANCE IMPACT
CREATE TABLE post_likes (
    explorer_id UUID NOT NULL REFERENCES users(id),
    post_id UUID NOT NULL REFERENCES posts(id),

    liked_at TIMESTAMPTZ NOT NULL ,
    liked BOOLEAN NOT NULL ,

    PRIMARY KEY (explorer_id, post_id)
);

-- SHOULD DELETED IF USER OR POST DELETE -- BUT I DO NOT IMPLEMENTED BECAUSE OF UN-KNOWN FUTURE PERFORMANCE IMPACT 
CREATE TABLE post_upvotes (
	promoter_id UUID NOT NULL REFERENCES users(id),
	post_id UUID NOT NULL REFERENCES posts(id),

	upvoted_at TIMESTAMPTZ NOT NULL,
	liked BOOLEAN NOT NULL ,

    PRIMARY KEY (promoter_id, post_id)
) 




