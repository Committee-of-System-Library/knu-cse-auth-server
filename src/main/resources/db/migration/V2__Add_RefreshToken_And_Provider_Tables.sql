-- V2__Add_RefreshToken_And_Provider_Tables.sql

CREATE TABLE refresh_token
(
    email VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (email)
);

CREATE TABLE provider
(
    provider_id bigint NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    provider_name VARCHAR(255) NOT NULL,
    provider_key VARCHAR(255) NOT NULL,
    student_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (provider_id),
    UNIQUE KEY provider_email_uq (email),
    CONSTRAINT provider_student_fk
        FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE SET NULL
);
