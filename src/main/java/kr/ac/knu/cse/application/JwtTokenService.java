package kr.ac.knu.cse.application;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import kr.ac.knu.cse.domain.client.AuthClient;
import kr.ac.knu.cse.domain.client.AuthClientRepository;
import kr.ac.knu.cse.domain.student.Student;
import kr.ac.knu.cse.domain.student.StudentRepository;
import kr.ac.knu.cse.domain.student.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private static final long TOKEN_VALIDITY_SECONDS = 3600;

    @Value("${app.jwt.issuer:https://chcse.knu.ac.kr/appfn/api}")
    private String issuerUrl;

    private final StudentRepository studentRepository;
    private final AuthClientRepository authClientRepository;

    @Transactional(readOnly = true)
    public String generateToken(Long studentId, String email, String clientName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        AuthClient client = authClientRepository.findByClientName(clientName)
                .orElseThrow(() -> new IllegalArgumentException("클라이언트를 찾을 수 없습니다."));

        UserType userType = student.getUserType();
        String studentNumber = student.getStudentNumber();
        Assert.notNull(userType,
                "student.userType must not be null (student_id=" + student.getId() + ")");
        Assert.hasText(studentNumber,
                "student.studentNumber must not be blank (student_id=" + student.getId() + ")");
        Assert.hasText(email,
                "email must not be blank (student_id=" + student.getId() + ")");

        // Pin HS256 explicitly. Keys.hmacShaKeyFor() picks an algorithm based
        // on key length (>=384 bits → HS384), which doesn't match the
        // resource server's NimbusJwtDecoder.withSecretKey default of HS256
        // and produced "Another algorithm expected" 401s.
        SecretKey key = new SecretKeySpec(
                client.getJwtSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(student.getId()))
                .claim("student_number", studentNumber)
                .claim("name", student.getName())
                .claim("email", email)
                .claim("major", student.getMajor())
                .claim("user_type", userType.name())
                .claim("role", student.getRole() != null ? student.getRole().name() : null)
                .audience().add(clientName).and()
                .issuer(issuerUrl)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(TOKEN_VALIDITY_SECONDS)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
