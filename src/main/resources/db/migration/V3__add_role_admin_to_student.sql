ALTER TABLE
    student
MODIFY
    role ENUM (
        'ROLE_EXECUTIVE',
        'ROLE_FINANCE',
        'ROLE_STUDENT',
        'ROLE_ADMIN'
    ) NOT NULL;