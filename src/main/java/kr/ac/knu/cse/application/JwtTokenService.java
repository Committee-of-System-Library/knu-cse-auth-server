package kr.ac.knu.cse.application;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import kr.ac.knu.cse.domain.client.AuthClient;
import kr.ac.knu.cse.domain.client.AuthClientRepository;
import kr.ac.knu.cse.domain.provider.Provider;
import kr.ac.knu.cse.domain.provider.ProviderRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private static final long TOKEN_VALIDITY_SECONDS = 3600;

    @Value("${app.jwt.issuer:https://chcse.knu.ac.kr/appfn/api}")
    private String issuerUrl;

    private final StudentRepository studentRepository;
    private final ProviderRepository providerRepository;
    private final AuthClientRepository authClientRepository;

    @Transactional(readOnly = true)
    public String generateToken(Long studentId, String email, String clientName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        AuthClient client = authClientRepository.findByClientName(clientName)
                .orElseThrow(() -> new IllegalArgumentException("클라이언트를 찾을 수 없습니다."));

        SecretKey key = Keys.hmacShaKeyFor(
                client.getJwtSecret().getBytes(StandardCharsets.UTF_8)
        );

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(student.getId()))
                .claim("student_number", student.getStudentNumber())
                .claim("name", student.getName())
                .claim("email", email)
                .claim("major", student.getMajor())
                .claim("user_type", student.getUserType().name())
                .claim("role", student.getRole() != null ? student.getRole().name() : null)
                .audience().add(clientName).and()
                .issuer(issuerUrl)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(TOKEN_VALIDITY_SECONDS)))
                .signWith(key)
                .compact();
    }
}
