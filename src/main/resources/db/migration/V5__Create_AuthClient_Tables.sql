-- 인증 클라이언트 테이블 생성
CREATE TABLE auth_clients
(
    client_id          BIGINT       NOT NULL AUTO_INCREMENT,
    client_name        VARCHAR(100) NOT NULL,
    client_description VARCHAR(500),
    jwt_secret         VARCHAR(500) NOT NULL,
    status             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',

    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL,

    PRIMARY KEY (client_id),
    UNIQUE KEY uq_client_name (client_name)
);

CREATE INDEX idx_client_status ON auth_clients (status);

-- 인증 클라이언트별 허용 도메인 테이블 생성
CREATE TABLE auth_client_allowed_domains
(
    client_id BIGINT       NOT NULL,
    domain    VARCHAR(255) NOT NULL,

    PRIMARY KEY (client_id, domain),
    CONSTRAINT fk_client_domains FOREIGN KEY (client_id) REFERENCES auth_clients (client_id) ON DELETE CASCADE
);

