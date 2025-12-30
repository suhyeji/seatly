ALTER TABLE study_cafe_member
RENAME TO user_study_cafe_link;

ALTER TABLE user_study_cafe_link
ADD COLUMN link_type VARCHAR(20) NOT NULL;

ALTER TABLE seat
ALTER COLUMN position TYPE TEXT;

ALTER TABLE time
RENAME TO user_time_pass;

ALTER TABLE user_time_pass
DROP CONSTRAINT time_pkey;

ALTER TABLE user_time_pass
DROP COLUMN study_cafe_member_id;

ALTER TABLE user_time_pass
DROP COLUMN left_time;

ALTER TABLE user_time_pass
ADD COLUMN left_time BIGINT;

ALTER TABLE user_time_pass
ADD COLUMN study_cafe_id BIGINT NOT NULL,
ADD COLUMN user_id BIGINT NOT NULL,
ADD COLUMN total_time BIGINT NOT NULL;

ALTER TABLE user_time_pass
ADD CONSTRAINT user_time_pass_pk
PRIMARY KEY (study_cafe_id, user_id);

ALTER TABLE user_time_pass
ADD CONSTRAINT fk_user_time_pass_study_cafe
FOREIGN KEY (study_cafe_id)
REFERENCES study_cafe(id);

ALTER TABLE user_time_pass
ADD CONSTRAINT fk_user_time_pass_user
FOREIGN KEY (user_id)
REFERENCES users(id);
