ALTER TABLE users
ADD CONSTRAINT uk_users_email UNIQUE (email);

ALTER TABLE users
ADD CONSTRAINT uk_users_phone UNIQUE (phone);

ALTER TABLE study_cafe
ADD CONSTRAINT uk_study_cafe_phone_number UNIQUE (phone_number);