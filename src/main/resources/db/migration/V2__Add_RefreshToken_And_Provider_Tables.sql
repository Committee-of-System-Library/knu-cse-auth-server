CREATE TABLE refresh_token (
    email varchar(255) NOT NULL,
    refresh_token varchar(255) NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (email)
);

CREATE TABLE provider (
    provider_id bigint NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    provider_name varchar(255) NOT NULL,
    provider_key varchar(255) NOT NULL,
    student_id bigint NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (provider_id),
    UNIQUE KEY provider_email_uq (email),
    CONSTRAINT provider_student_fk FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE
    SET
        NULL
);