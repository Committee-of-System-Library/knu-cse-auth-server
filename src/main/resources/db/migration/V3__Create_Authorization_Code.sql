CREATE TABLE authorization_codes (
    code VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    redirect_uri VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_authorization_codes_email ON authorization_codes(email);
CREATE INDEX idx_authorization_codes_expires_at ON authorization_codes(expires_at);
CREATE INDEX idx_authorization_codes_used ON authorization_codes(used);