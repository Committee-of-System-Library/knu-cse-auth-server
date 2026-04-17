CREATE TABLE snack_event (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    semester VARCHAR(10) NOT NULL,
    requires_payment BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    opened_at DATETIME NOT NULL,
    closed_at DATETIME NULL,
    opened_by_student_number VARCHAR(20) NULL,
    PRIMARY KEY (id),
    INDEX idx_snack_event_status (status),
    INDEX idx_snack_event_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE snack_handout (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    student_number VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    major VARCHAR(100) NULL,
    received_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_snack_handout_event_student (event_id, student_number),
    INDEX idx_snack_handout_event (event_id),
    CONSTRAINT fk_snack_handout_event FOREIGN KEY (event_id) REFERENCES snack_event (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
