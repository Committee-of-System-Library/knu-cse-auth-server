-- V13 에서 cse_student_registry 를 2026-03-30 학부 데이터로 갱신했음.
-- 기존에 가입한 CSE_STUDENT 의 student.major 를 registry 기준으로 일치시킨다.
-- 신규 가입자는 SignupService 가 registry.major 로 override 하므로 이후엔 자동 동기화.
UPDATE student s
INNER JOIN cse_student_registry r
    ON s.student_number = r.student_number
SET s.major = r.major,
    s.updated_at = NOW()
WHERE s.user_type = 'CSE_STUDENT'
  AND s.deleted_at IS NULL
  AND r.major IS NOT NULL
  AND (s.major IS NULL OR s.major <> r.major);
