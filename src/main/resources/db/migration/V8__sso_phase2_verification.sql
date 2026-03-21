-- verification_request 테이블 생성
CREATE TABLE verification_request (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id               BIGINT NOT NULL,
    requested_student_number VARCHAR(20) NOT NULL,
    evidence_description     TEXT,
    status                   VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewer_id              BIGINT,
    review_comment           TEXT,
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at              TIMESTAMP,

    CONSTRAINT fk_verify_student FOREIGN KEY (student_id) REFERENCES student(student_id),
    CONSTRAINT fk_verify_reviewer FOREIGN KEY (reviewer_id) REFERENCES student(student_id)
);
