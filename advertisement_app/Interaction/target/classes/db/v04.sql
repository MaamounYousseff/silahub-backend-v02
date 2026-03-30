CREATE TABLE post_likes (
    explorer_id UUID NOT NULL,
    post_interaction_id UUID NOT NULL,
    liked_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    liked BOOLEAN NOT NULL,

    CONSTRAINT post_likes_pkey
        PRIMARY KEY (explorer_id, post_interaction_id),

    CONSTRAINT fk_explorer_like
        FOREIGN KEY (explorer_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_post_interaction_like
        FOREIGN KEY (post_interaction_id)
        REFERENCES post_interactions(post_id)
        ON DELETE CASCADE
);


CREATE TABLE post_upvotes (
    promoter_id UUID NOT NULL,
    post_interaction_id UUID NOT NULL,
    upvoted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    upvoted BOOLEAN NOT NULL,

    CONSTRAINT post_upvotes_pkey
        PRIMARY KEY (promoter_id, post_interaction_id),

    CONSTRAINT fk_promoter_upvote
        FOREIGN KEY (promoter_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_post_interaction_upvote
        FOREIGN KEY (post_interaction_id)
        REFERENCES post_interactions(post_id)
        ON DELETE CASCADE
);


--_____________________________________________
--no change
CREATE TABLE post_interactions (
    post_id UUID PRIMARY KEY REFERENCES posts(id) ON DELETE CASCADE,
    total_views BIGINT NOT NULL ,
    total_upvotes BIGINT NOT NULL ,
    total_likes BIGINT NOT NULL ,
    total_watch_seconds BIGINT NOT NULL ,
    total_clicks BIGINT NOT NULL
);