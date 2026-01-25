CREATE TABLE users (
    id UUID PRIMARY KEY,

    username VARCHAR NOT NULL UNIQUE,

    first_name VARCHAR,
    last_name VARCHAR,
    phone_number VARCHAR,
    whatsapp_number VARCHAR,
    logo_url TEXT,

    rank SMALLINT,
    latitude REAL,
    longitude REAL,

    account_non_locked BOOLEAN NOT NULL,
    is_enabled BOOLEAN NOT NULL,

    login_attempts SMALLINT NOT NULL DEFAULT 0,
    last_login TIMESTAMP,

    created_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL,
    updated_by UUID,

    CONSTRAINT users_created_by_fkey
        FOREIGN KEY (created_by) REFERENCES users(id),

    CONSTRAINT users_updated_by_fkey
        FOREIGN KEY (updated_by) REFERENCES users(id)
);



CREATE TABLE roles (
    id UUID PRIMARY KEY,

    name VARCHAR NOT NULL UNIQUE,

    authorities TEXT,

    is_active BOOLEAN NOT NULL DEFAULT true
);


CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT user_roles_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT user_roles_role_id_fkey
        FOREIGN KEY (role_id) REFERENCES roles(id)
);
