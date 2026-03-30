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
    explorer_id UUID NOT NULL,
    post_interaction_id UUID NOT NULL,
    liked_at TIMESTAMPTZ NOT NULL,
    liked BOOLEAN NOT NULL,
    PRIMARY KEY (explorer_id, post_interaction_id),
    CONSTRAINT fk_explorer_like FOREIGN KEY (explorer_id) REFERENCES users(id) ,
    CONSTRAINT fk_post_interaction_like FOREIGN KEY (post_interaction_id) REFERENCES post_interactions("post_id")
);

-- SHOULD DELETED IF USER OR POST DELETE -- BUT I DO NOT IMPLEMENTED BECAUSE OF UN-KNOWN FUTURE PERFORMANCE IMPACT
CREATE TABLE post_upvotes (
    promoter_id UUID NOT NULL,
    post_interaction_id UUID NOT NULL,
    upvoted_at TIMESTAMPTZ NOT NULL,
    liked BOOLEAN NOT NULL,
    PRIMARY KEY (promoter_id, post_interaction_id),
    CONSTRAINT fk_promoter_upvote FOREIGN KEY (promoter_id) REFERENCES users(id) ,
    CONSTRAINT fk_post_interaction_upvote FOREIGN KEY (post_interaction_id) REFERENCES post_interactions("post_id")
);




