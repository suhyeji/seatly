CREATE TABLE study_cafe (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(50) NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    role             INT NOT NULL,              -- 0: 사용자, 1: 관리자 (enum numeric)
    last_login_type  VARCHAR(50)
);

CREATE TABLE study_cafe_member (
    id             BIGSERIAL PRIMARY KEY,
    study_cafe_id  BIGINT NOT NULL,
    user_id        BIGINT NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_scm_study_cafe
        FOREIGN KEY (study_cafe_id)
        REFERENCES study_cafe(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_scm_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_scm_unique
        UNIQUE (study_cafe_id, user_id)
);

CREATE TABLE seat (
    id             BIGSERIAL PRIMARY KEY,
    study_cafe_id  BIGINT NOT NULL,
    seat_number    INT NOT NULL,
    position       VARCHAR(20) NOT NULL,
    status         VARCHAR(20) NOT NULL,   -- AVAILABLE / ASSIGNED / IN_USE
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_seat_study_cafe
        FOREIGN KEY (study_cafe_id)
        REFERENCES study_cafe(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_seat_unique
        UNIQUE (study_cafe_id, seat_number)
);

CREATE TABLE time (
    study_cafe_member_id BIGINT PRIMARY KEY,
    left_time            TIMESTAMP NOT NULL,

    CONSTRAINT fk_time_member
        FOREIGN KEY (study_cafe_member_id)
        REFERENCES study_cafe_member(id)
        ON DELETE CASCADE
);

CREATE TABLE session (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    seat_id     BIGINT NOT NULL,
    status      VARCHAR(20) NOT NULL,   -- REQUESTED / ASSIGNED / IN_USE / FINISHED / FORCED
    start_time  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_session_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_session_seat
        FOREIGN KEY (seat_id)
        REFERENCES seat(id)
        ON DELETE CASCADE
);

-- StudyCafeMember 조회
CREATE INDEX idx_scm_user_id ON study_cafe_member(user_id);
CREATE INDEX idx_scm_study_cafe_id ON study_cafe_member(study_cafe_id);

-- Seat 조회
CREATE INDEX idx_seat_study_cafe_id ON seat(study_cafe_id);
CREATE INDEX idx_seat_status ON seat(status);

-- Session 조회
CREATE INDEX idx_session_user_id ON session(user_id);
CREATE INDEX idx_session_seat_id ON session(seat_id);
CREATE INDEX idx_session_status ON session(status);
