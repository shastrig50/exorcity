-- Exorcity initial schema

CREATE TABLE responsible_entities (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    type        VARCHAR(30)  NOT NULL,
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    ward        VARCHAR(100) NOT NULL,
    civic_score INTEGER NOT NULL DEFAULT 600 CHECK (civic_score BETWEEN 0 AND 900)
);

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    civic_score   INTEGER NOT NULL DEFAULT 600 CHECK (civic_score BETWEEN 0 AND 900),
    entity_id     BIGINT REFERENCES responsible_entities (id),
    ward          VARCHAR(100),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE garbage_spots (
    id                          BIGSERIAL PRIMARY KEY,
    type                        VARCHAR(30)  NOT NULL,
    description                 VARCHAR(1000),
    latitude                    DOUBLE PRECISION NOT NULL,
    longitude                   DOUBLE PRECISION NOT NULL,
    before_photo_url            VARCHAR(1000) NOT NULL,
    after_photo_url             VARCHAR(1000),
    status                      VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    reported_by                 BIGINT NOT NULL REFERENCES users (id),
    cleaned_by                  BIGINT REFERENCES users (id),
    linked_entity_id            BIGINT REFERENCES responsible_entities (id),
    ward                        VARCHAR(100),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    cleaned_at                  TIMESTAMPTZ,
    confirmation_count          INTEGER NOT NULL DEFAULT 0,
    cursed                      BOOLEAN NOT NULL DEFAULT FALSE,
    first_confirmation_rewarded BOOLEAN NOT NULL DEFAULT FALSE,
    curse_penalty_applied       BOOLEAN NOT NULL DEFAULT FALSE,
    overdue_penalty_applied     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_spots_status   ON garbage_spots (status);
CREATE INDEX idx_spots_ward     ON garbage_spots (ward);
CREATE INDEX idx_spots_lat_lng  ON garbage_spots (latitude, longitude);
CREATE INDEX idx_spots_entity   ON garbage_spots (linked_entity_id);

CREATE TABLE confirmations (
    id         BIGSERIAL PRIMARY KEY,
    spot_id    BIGINT NOT NULL REFERENCES garbage_spots (id) ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES users (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_confirmation_spot_user UNIQUE (spot_id, user_id)
);

CREATE TABLE score_events (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users (id),
    entity_id   BIGINT REFERENCES responsible_entities (id),
    spot_id     BIGINT REFERENCES garbage_spots (id) ON DELETE SET NULL,
    points      INTEGER NOT NULL,
    reason      VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_score_event_owner CHECK ((user_id IS NULL) <> (entity_id IS NULL))
);

CREATE INDEX idx_score_events_user   ON score_events (user_id);
CREATE INDEX idx_score_events_entity ON score_events (entity_id);
