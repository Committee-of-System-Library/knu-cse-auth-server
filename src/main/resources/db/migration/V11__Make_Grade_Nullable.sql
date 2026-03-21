-- 학년(grade) 필드를 nullable로 변경 (회원가입 시 학년 입력 제거)
ALTER TABLE student MODIFY COLUMN grade VARCHAR(15) NULL;
