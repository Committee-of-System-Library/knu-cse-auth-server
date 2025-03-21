package kr.ac.knu.cse.global.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieGenerator {
	public Cookie generateCookie(String name, String value, int expiry) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setMaxAge(expiry);
		return cookie;
	}
}
