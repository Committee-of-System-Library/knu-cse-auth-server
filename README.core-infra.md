# knu-cse-auth-server (core-infra)

`knu-cse-core-infra/server-setup/knu-cse-auth-server` 기준 배포 가이드입니다.

## 요구 사항

- Docker Engine 24+
- Docker Compose v2.20+
- Java 17+ 
- MySQL 8.0+ 
- Keycloak 26.x (realm/IdP 설정 호환 기준)
- Google Cloud Console OAuth 클라이언트 접근 권한 (Google IdP 설정용)
- GHCR push 권한 (이미지 배포 시)

## 1) 설정 파일 준비

core-infra auth-server 폴더로 이동:

```bash
cd ../knu-cse-core-infra/server-setup/knu-cse-auth-server
```

`.env` 생성(템플릿 복사):

```bash
cp /path/to/knu-cse-auth-server/.env.example .env
```

복사한 `.env`를 core-infra 환경값(DB/포트/도메인/시크릿)으로 수정합니다.

## 2) Google Console 설정 (필수)

Google Cloud Console OAuth 클라이언트에 아래 Redirect URI를 등록합니다.

- `https://<SSO_HOST>/realms/cse-sso/broker/google/endpoint`

예시:

- `https://sso.example.com/realms/cse-sso/broker/google/endpoint`

Google Console에서 발급된 값을 `.env`에 입력합니다.

- Google Console `Client ID` -> `GOOGLE_IDP_CLIENT_ID`
- Google Console `Client secret` -> `GOOGLE_IDP_CLIENT_SECRET`

## 3) 최초 배포 (빌드 + Keycloak 초기 세팅 + 실행)

1. `knu-cse-auth-server`에서 이미지 빌드/푸시:

```bash
cd /path/to/knu-cse-auth-server
./gradlew clean bootJar
docker build -t ghcr.io/committee-of-system-library/knu-cse-auth-server:latest .
docker push ghcr.io/committee-of-system-library/knu-cse-auth-server:latest
```

2. Keycloak 초기 세팅 1회 실행:

```bash
docker compose --profile init --env-file .env.standalone -f docker-compose.standalone.yml up -d keycloak keycloak-init
```

3. core-infra auth-server 실행:

```bash
cd ../knu-cse-core-infra/server-setup/knu-cse-auth-server
docker compose -f docker-compose.yaml pull
docker compose -f docker-compose.yaml up -d
```

## 4) 이후 배포 (이미지 갱신)

코드 변경 시, 이미지를 다시 빌드/푸시 후 배포합니다.

```bash
cd /path/to/knu-cse-auth-server
./gradlew clean bootJar
docker build -t ghcr.io/committee-of-system-library/knu-cse-auth-server:latest .
docker push ghcr.io/committee-of-system-library/knu-cse-auth-server:latest

cd ../knu-cse-core-infra/server-setup/knu-cse-auth-server
docker compose -f docker-compose.yaml pull
docker compose -f docker-compose.yaml up -d
```

## 5) 중지

```bash
docker compose -f docker-compose.yaml down
```
