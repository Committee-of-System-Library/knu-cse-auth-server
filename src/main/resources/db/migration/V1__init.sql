CREATE TABLE student
(
    student_id     bigint      NOT NULL AUTO_INCREMENT,
    role           varchar(20) NOT NULL,
    major          varchar(50) NOT NULL,
    name           varchar(50) NOT NULL,
    student_number varchar(15) NOT NULL,
    grade          varchar(15) NOT NULL, -- 추가
    created_at     timestamp   NOT NULL,
    updated_at     timestamp   NOT NULL,

    PRIMARY KEY (student_id),
    UNIQUE KEY student_number_uq (student_number)
);

CREATE TABLE dues
(
    dues_id             bigint      NOT NULL AUTO_INCREMENT,
    student_id          bigint      NOT NULL, -- 조회 최적화를 위해서 있으면 좋으려나?
    student_number      varchar(15) NOT NULL,
    semesters           varchar(15) NOT NULL,
    is_paid             TINYINT(1)  NOT NULL DEFAULT FALSE,
    created_at          timestamp   NOT NULL,
    updated_at          timestamp   NOT NULL,

    PRIMARY KEY (dues_id)
);
-- student_id, student_number 인덱스 걸 예정