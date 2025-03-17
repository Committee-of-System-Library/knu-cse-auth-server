CREATE TABLE student
(
    student_id     bigint                                                NOT NULL AUTO_INCREMENT,
    role           enum ('ROLE_EXECUTIVE','ROLE_FINANCE','ROLE_STUDENT') NOT NULL,
    major          enum ('PLATFORM', 'AI', 'GLOBAL')                     NOT NULL,
    name           varchar(50)                                           NOT NULL,
    student_number varchar(15)                                           NOT NULL,
    created_at     timestamp                                             NOT NULL,
    updated_at     timestamp                                             NOT NULL,
    PRIMARY KEY (student_id),
    UNIQUE KEY student_number_uq (student_number)
);

CREATE TABLE dues
(
    dues_id             bigint      NOT NULL AUTO_INCREMENT,
    student_id          bigint      NOT NULL,
    depositor_name      varchar(50) NOT NULL,
    amount              int         NOT NULL,
    remaining_semesters int         NOT NULL,
    submitted_at        timestamp   NOT NULL,
    created_at          timestamp   NOT NULL,
    updated_at          timestamp   NOT NULL,
    PRIMARY KEY (dues_id),
    FOREIGN KEY (student_id) REFERENCES student (student_id)
);
