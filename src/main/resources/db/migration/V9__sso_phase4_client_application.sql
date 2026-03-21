-- client_application 테이블 생성
CREATE TABLE client_application (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_name             VARCHAR(100) NOT NULL,
    description          TEXT,
    client_id            VARCHAR(255) UNIQUE,
    client_secret_hash   VARCHAR(255),
    redirect_uris        JSON NOT NULL,
    homepage_url         VARCHAR(500),
    owner_id             BIGINT NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rejection_reason     TEXT,
    keycloak_client_uuid VARCHAR(255),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_client_app_owner FOREIGN KEY (owner_id) REFERENCES student(student_id)
);
