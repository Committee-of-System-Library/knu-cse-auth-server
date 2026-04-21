package kr.ac.knu.cse.presentation;

import java.util.List;
import kr.ac.knu.cse.application.AdminAuthService;
import kr.ac.knu.cse.presentation.dto.ArchitectureDocResponse;
import kr.ac.knu.cse.presentation.dto.ArchitectureDocResponse.Block;
import kr.ac.knu.cse.presentation.dto.ArchitectureDocResponse.Row;
import kr.ac.knu.cse.presentation.dto.ArchitectureDocResponse.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/developer/docs")
public class DeveloperDocsController {

    private static final String UPDATED_AT = "2026-04-21";

    private final AdminAuthService adminAuthService;

    @GetMapping("/architecture")
    public ResponseEntity<ArchitectureDocResponse> getArchitecture(
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        adminAuthService.requireStaff(oidcUser);
        return ResponseEntity.ok(new ArchitectureDocResponse(UPDATED_AT, architectureSections()));
    }

    private List<Section> architectureSections() {
        return List.of(
                overview(),
                runtimeTopology(),
                backendMatrix(),
                frontendMatrix(),
                deploymentFlow(),
                nginxRouting(),
                operationalRisks()
        );
    }

    private Section overview() {
        return new Section(
                "overview",
                "플랫폼 개요",
                "3개 사용자 서비스 + SSO IdP + 공유 라이브러리로 구성된 모노레포.",
                List.of(
                        bullets("서비스 구성", List.of(
                                "auth-server + auth-client — SSO IdP, 로그인, 관리자/개발자 포털",
                                "ledger-server + ledger-client — 학생회비 납부 관리",
                                "locker-server + locker-client — 사물함 배정",
                                "knu-cse-sso-spring-boot-starter — ledger/locker 가 JWT 검증에 사용하는 공유 라이브러리"
                        )),
                        bullets("공통 원칙", List.of(
                                "auth-server 가 IdP, HMAC-SHA256 JWT 1h TTL 발급",
                                "ledger/locker 는 starter 의 자동설정으로 대칭키 검증",
                                "프론트 3종은 Nginx 서브패스(/appfn, /ledger, /locker) 정적 서빙",
                                "외부 도메인 단일: chcse.knu.ac.kr"
                        ))
                )
        );
    }

    private Section runtimeTopology() {
        return new Section(
                "topology",
                "런타임 토폴로지",
                "Docker 네트워크 + 컨테이너 역할 분담.",
                List.of(
                        bullets("네트워크", List.of(
                                "cse-proxy — Nginx 와 앱 컨테이너의 접점",
                                "cse-backend — 앱 ↔ DB / Keycloak 내부 통신",
                                "serv_DB — DB 전용 네트워크"
                        )),
                        bullets("공유 인프라", List.of(
                                "reverse proxy: nginx 1.29 (SSL 종단, 서브패스 라우팅)",
                                "IdP: Keycloak 26 (auth-server 가 OAuth2 Client 로 위임)",
                                "DB: MySQL 9.4 (스키마 3종 — cse_auth, cse_ledger, cse_locker)"
                        ))
                )
        );
    }

    private Section backendMatrix() {
        return new Section(
                "backend-matrix",
                "백엔드 서비스 차이",
                "3개 서버의 스택·빌드 방식 비교. 정당성이 약한 항목은 정리 대상.",
                List.of(
                        table(
                                "구성 요소 매트릭스",
                                List.of("항목", "auth-server", "ledger-server", "locker-server"),
                                List.of(
                                        row("Java / Spring Boot", "17 / 3.4.2", "21 / 4.0.2", "21 / 4.0.2"),
                                        row("Gradle DSL", "Groovy", "Groovy", "Kotlin"),
                                        row("Base image", "amazoncorretto:17", "amazoncorretto:21", "eclipse-temurin:21"),
                                        row("Docker stages", "단일", "단일", "multi-stage"),
                                        row("SSO starter", "N/A (IdP)", "1.2.0", "1.2.0"),
                                        row("Context-path", "/appfn/api", "/api/ledger", "/api/locker"),
                                        row("APM(Pinpoint)", "O", "X", "X"),
                                        row("Healthcheck", "X", "X", "X")
                                )
                        ),
                        bullets("정당한 차이", List.of(
                                "auth-server Java 17: Spring Boot 3.4.2 가 17 요구. SB 4 로 마이그레이션 점진적",
                                "auth-server Pinpoint 전용: SSO 핵심 경로라 APM 우선 적용",
                                "locker-server multi-stage: 빌드를 이미지 안으로 격리해 재현성 확보"
                        )),
                        bullets("정리 대상", List.of(
                                "locker-server Kotlin DSL / eclipse-temurin: 다른 두 서버와 일관성 없음",
                                "ledger-server MANAGE_SERVER_PORT 네이밍: {SERVICE}_SERVER_PORT 패턴 미준수",
                                "전 서버 healthcheck 부재: /actuator/health 기반 도입 권장"
                        ))
                )
        );
    }

    private Section frontendMatrix() {
        return new Section(
                "frontend-matrix",
                "프론트엔드 서비스 차이",
                "3개 클라이언트의 빌드·환경변수 주입 방식 비교.",
                List.of(
                        table(
                                "구성 요소 매트릭스",
                                List.of("항목", "auth-client", "ledger-client", "locker-client"),
                                List.of(
                                        row("Package manager", "pnpm@9.15.0", "pnpm", "npm"),
                                        row("Vite", "7", "7", "6"),
                                        row("Node builder", "20-alpine", "22-alpine", "22-alpine"),
                                        row("Env 주입", "placeholder + sed", "build-arg hardcode", "build-arg hardcode"),
                                        row("compose environment:", "O", "X", "X")
                                )
                        ),
                        bullets("정당한 차이", List.of(
                                "auth-client placeholder + sed: Developer Portal 다환경 확장 여지. 런타임 치환으로 이미지 재사용 가능",
                                "ledger/locker-client build-arg: 단일 prod 환경 전제, 값이 브라우저 번들에 embed 되어도 비밀 아님"
                        )),
                        bullets("정리 대상", List.of(
                                "locker-client npm + Vite 6: ledger-client 대비 1 버전 뒤. pnpm + Vite 7 로 동일화 필요"
                        ))
                )
        );
    }

    private Section deploymentFlow() {
        return new Section(
                "deployment",
                "배포 흐름",
                "개별 서비스 배포 + 인프라 배포의 2 트랙.",
                List.of(
                        bullets("개별 서비스 (auth / ledger / locker × server·client)", List.of(
                                "개발자 git push → 서비스 레포 main",
                                "GitHub Actions (ubuntu-latest) — Gradle/Vite 빌드 + Docker build + GHCR push",
                                "self-hosted runner (backend|frontend label) — docker pull + compose up -d <service>"
                        )),
                        bullets("인프라 (core-infra)", List.of(
                                "core-infra main push → paths 필터로 infra/services/nginx 변경 감지",
                                "self-hosted runner (infra label) — git pull 로 compose + nginx dir 동기화",
                                "변경 종류별로 compose up / nginx -s reload 선택 실행"
                        )),
                        bullets("self-hosted runner 라벨", List.of(
                                "infra — core-infra 전용",
                                "backend — Java 서비스 4종",
                                "frontend — React/Vite 클라이언트 4종"
                        ))
                )
        );
    }

    private Section nginxRouting() {
        return new Section(
                "nginx",
                "Nginx 라우팅",
                "외부 경로가 어떤 컨테이너로 흘러가는지의 매핑.",
                List.of(
                        table(
                                "외부 경로 → upstream",
                                List.of("외부 경로", "컨테이너", "비고"),
                                List.of(
                                        row("/", "comit-client (fallback)", "메인 랜딩"),
                                        row("/appfn", "auth-client", "정적, CSP 헤더 추가"),
                                        row("/appfn/api/", "auth-server", "X-Forwarded-* 세팅"),
                                        row("/appfn/api/realms", "keycloak", "URI rewrite 후 전달"),
                                        row("/ledger/", "ledger-client", "SPA try_files"),
                                        row("/api/ledger/", "ledger-server", "context-path 매칭"),
                                        row("/locker", "locker-client", "SPA try_files"),
                                        row("/api/locker/", "locker-server", "신 경로 (권장)"),
                                        row("/locker/api/", "locker-server (rewrite)", "구 경로 호환"),
                                        row("/api/*/internal/", "403", "common-headers.conf 차단")
                                )
                        ),
                        bullets("구조적 특징", List.of(
                                "모든 proxy_pass 는 변수 기반 — 컨테이너 recreate 시 nginx reload 불필요",
                                "nginx 설정 디렉터리 전체가 bind mount — 단일 파일 mount 의 inode 고정 이슈 회피"
                        ))
                )
        );
    }

    private Section operationalRisks() {
        return new Section(
                "risks",
                "운영 유의 사항",
                "현재 구조의 암묵적 전제 및 잠재 리스크.",
                List.of(
                        bullets("주의", List.of(
                                "Pinpoint agent 경로 누락 시 auth-server 부팅 실패 (javaagent 필수)",
                                "env 파일은 git 미포함 — 서버 소실 시 .env.example 기반 재구축",
                                "auth-server forward-headers-strategy 와 context-path 동시 유효, 값 일치 전제"
                        )),
                        bullets("알려진 부채", List.of(
                                "모든 backend healthcheck 부재",
                                "DB runtime user 에 ALTER 권한 상시 보유 (Flyway 용, 최소권한 관점에서 재검토 필요)",
                                "CURRENT_ISSUES.md 의 P4 Partial (엔티티 직접 JSON 응답) — DTO 분리 미완"
                        ))
                )
        );
    }

    private Block bullets(String heading, List<String> items) {
        return new Block("bullets", heading, items, null, null);
    }

    private Block table(String heading, List<String> headers, List<Row> rows) {
        return new Block("table", heading, null, rows, headers);
    }

    private Row row(String... cells) {
        return new Row(List.of(cells));
    }
}
