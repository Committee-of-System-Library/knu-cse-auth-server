CREATE TABLE qr_auth_log
(
    qr_auth_log_id             BIGINT       NOT NULL AUTO_INCREMENT,
    scan_date      DATE         NOT NULL,
    student_number VARCHAR(15)  NOT NULL,
    student_name   VARCHAR(50)  NOT NULL,
    dues_paid      BOOLEAN      NOT NULL,
    scanned_by     VARCHAR(255) NOT NULL,

    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (qr_auth_log_id),
    UNIQUE KEY uq_qr_auth_log (scan_date, student_number, scanned_by)
);
