package kr.ac.knu.cse.infrastructure.security.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.ac.knu.cse.global.exception.BusinessException;
import kr.ac.knu.cse.global.exception.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class FilterBusinessExceptionWriter {

    private final ObjectMapper objectMapper;

    public void write(
            HttpServletResponse response,
            BusinessException e
    ) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .build();

        String body = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(body);
    }
}
