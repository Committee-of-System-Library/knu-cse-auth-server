-- 1) Student 테이블에 user_type 추가
ALTER TABLE student
    ADD COLUMN user_type VARCHAR(20) NOT NULL DEFAULT 'EXTERNAL';

-- 2) 기존 데이터 마이그레이션: 기존 사용자는 CSE_STUDENT + 새 Role로 처리
UPDATE student SET user_type = 'CSE_STUDENT', role = 'STUDENT'   WHERE role = 'ROLE_USER';
UPDATE student SET user_type = 'CSE_STUDENT', role = 'ADMIN'     WHERE role = 'ROLE_ADMIN';
UPDATE student SET user_type = 'CSE_STUDENT', role = 'EXECUTIVE' WHERE role = 'ROLE_EXEC';
UPDATE student SET user_type = 'CSE_STUDENT', role = 'FINANCE'   WHERE role = 'ROLE_FINANCE';

-- 3) Student 테이블의 role을 nullable로 변경
ALTER TABLE student
    MODIFY COLUMN role VARCHAR(20) NULL DEFAULT NULL;

-- 4) cse_student_registry 테이블 생성
CREATE TABLE cse_student_registry (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_number    VARCHAR(20)  NOT NULL UNIQUE,
    name              VARCHAR(50)  NOT NULL,
    major             VARCHAR(100) NULL,
    grade             INT          NULL,
    is_manually_added BOOLEAN DEFAULT FALSE,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
