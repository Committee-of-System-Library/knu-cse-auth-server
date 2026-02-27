package kr.ac.knu.cse.infrastructure.security.support;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieCreator {

    @Value("${app.security.cookie.name:ACCESS_TOKEN}")
    private String cookieAccessTokenName;
    @Value("${app.security.cookie.path:/}")
    private String cookiePathValue;
    @Value("${app.security.cookie.max-age-seconds:3600}")
    private int cookieMaxAge;
    @Value("${app.security.cookie.same-site:LAX}")
    private String cookieSameSiteValue;
    @Value("${app.security.cookie.http-only:true}")
    private boolean cookieHttpOnly;
    @Value("${app.security.cookie.secure:true}")
    private boolean cookieSecure;

    public ResponseCookie createWithValue(String value) {
        return ResponseCookie.from(cookieAccessTokenName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path(cookiePathValue)
                .maxAge(cookieMaxAge)
                .sameSite(cookieSameSiteValue)
                .build();
    }

    public ResponseCookie create() {
        return ResponseCookie.from(cookieAccessTokenName, "value")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path(cookiePathValue)
                .maxAge(0)
                .sameSite(cookieSameSiteValue)
                .build();
    }
}
