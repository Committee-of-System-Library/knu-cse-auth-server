-- 내부 SSO 클라이언트 (Admin/Developer 콘솔 로그인용)
INSERT INTO auth_clients (client_name, client_description, jwt_secret, status, created_at, updated_at)
VALUES ('cse-internal',
        'Internal SSO client for Admin/Developer console',
        'JyCbR4o58o8yvWrLlk7dHQLhx0zpLLUHwn5/QHty8vKDn27rRLBztbwgnFhDa341n7rdo5D0CkXrFndY9nHf/g==',
        'ACTIVE',
        NOW(),
        NOW());

-- 허용 도메인
INSERT INTO auth_client_allowed_domains (client_id, domain)
SELECT client_id, 'chcse.knu.ac.kr'
FROM auth_clients
WHERE client_name = 'cse-internal';

INSERT INTO auth_client_allowed_domains (client_id, domain)
SELECT client_id, 'localhost'
FROM auth_clients
WHERE client_name = 'cse-internal';
