# KNU-CSE Auth Server

경북대학교 컴퓨터학부 시도위 통합 인증/인가 백엔드입니다.

core-infra 배포 가이드는 `README.core-infra.md`를 참고하세요.

## 개요

- Keycloak 기반 OAuth2/OIDC 로그인 제공(기본 IdP: Google)
- 로그인/로그아웃 및 OAuth2 콜백 처리
- 신규 사용자 회원가입 및 사용자 식별 정보 연동
- 권한 동기화 및 내부 연동 API 제공
- 일관된 예외 응답과 환경별 설정 분리 지원

## 프로젝트 구조

```text
src/main/java/kr/ac/knu/cse/
├── application/
│   └── dto/
├── domain/
│   ├── due/
│   ├── provider/
│   ├── redirect/
│   ├── role/
│   └── student/
├── infrastructure/
│   ├── keycloak/
│   │   └── config/
│   └── security/
│       ├── config/
│       ├── filter/
│       └── support/
├── presentation/
│   └── dto/
└── global/
    ├── base/
    ├── config/
    └── exception/
```

## 요구 사항

- Docker Engine 24+
- Docker Compose v2.20+
- Java 17+
- MySQL 8.0+ 
- Keycloak 26.5.2 
- Google Cloud Console OAuth 클라이언트 접근 권한 (Google IdP 설정용)

## 1) 설정 파일 만들기

```bash
cp .env.standalone.example .env.standalone
```

`.env.standalone`에 필요한 값(시크릿/포트/도메인)을 채웁니다.

## 2) 빌드하기

```bash
./gradlew clean bootJar
```

`docker-compose.standalone.yml`은 `build/libs/*.jar`를 사용하므로 실행 전에 JAR를 먼저 빌드해야 합니다.

## 3) Google Console 설정 (필수)

Google Cloud Console OAuth 클라이언트에 아래 Redirect URI를 등록합니다.

- `http://localhost:8080/realms/cse-sso/broker/google/endpoint`

Google Console에서 발급된 값을 `.env.standalone`에 입력합니다.

- Google Console `Client ID` -> `GOOGLE_IDP_CLIENT_ID`
- Google Console `Client secret` -> `GOOGLE_IDP_CLIENT_SECRET`

## 4) 실행

최초 실행:

```bash
docker compose --profile init --env-file .env.standalone -f docker-compose.standalone.yml up -d --build
```

최초 1회는 `keycloak-init`가 실행되어 Keycloak 초기 세팅(클라이언트/권한/Google IdP 값 반영)을 수행합니다.

일반 실행:

```bash
docker compose --env-file .env.standalone -f docker-compose.standalone.yml up -d --build
```

초기 세팅이 끝난 뒤에는 `init` 없이 실행합니다.

## 5) Keycloak 토큰 만료 기본값

`keycloak/import/cse-sso-realm.json`의 기본 만료 정책은 아래와 같습니다.

- Access Token: 300초 (5분)
- SSO Session Idle Timeout: 3600초 (1시간)
- SSO Session Max Lifespan: 28800초 (8시간)
- Offline Session Idle/Max Lifespan: 2592000초 (30일)
- OAuth Authorization Code(Login): 300초 (5분)

## 6) 중지 / 초기화

중지:

```bash
docker compose --env-file .env.standalone -f docker-compose.standalone.yml down
```

데이터까지 초기화:

```bash
docker compose --env-file .env.standalone -f docker-compose.standalone.yml down -v
```

## 7) 관련 파일

- Compose: `docker-compose.standalone.yml`
- Env template: `.env.standalone.example`
- Env local: `.env.standalone`
- Realm import: `keycloak/import/cse-sso-realm.json`
- Bootstrap script: `keycloak/init/bootstrap-clients.sh`
