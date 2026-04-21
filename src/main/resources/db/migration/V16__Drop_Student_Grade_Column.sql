-- 학생(student) 테이블의 grade 컬럼 제거.
-- e15ecd4 (2026-03-21) 에서 회원가입 학년 입력 필드가 제거된 이후 신규 가입은 항상 NULL 로 저장되고,
-- 애플리케이션 코드 어디에서도 Student.getGrade() 를 읽지 않아 dead column 상태였음.
-- F5 (2026-04-21) 와 같이 잘못된 값이 enum 매핑을 깨뜨려 admin 전체 500 을 유발하는 위험을 제거한다.
-- 학년 정보는 cse_student_registry.grade (INT) 만 신뢰 소스로 유지된다.
ALTER TABLE student DROP COLUMN grade;
