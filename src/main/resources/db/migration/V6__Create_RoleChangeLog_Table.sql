CREATE TABLE role_change_log (
    role_change_log_id  bigint      NOT NULL AUTO_INCREMENT,
    student_id          bigint      NOT NULL,
    before_role         varchar(20) NOT NULL,
    after_role          varchar(20) NOT NULL,
    changed_at          timestamp   NOT NULL,
    processed_at        timestamp   NULL,

    PRIMARY KEY (role_change_log_id)
);