package kr.ac.knu.cse.security.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.student.domain.Student;
import lombok.Builder;

@Builder
public record PrincipalDetails(
	Student student,
	Provider provider,
	Map<String, Object> attributes
) implements OAuth2User {
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (student != null && student.getRole() != null) {
			authorities.add(new SimpleGrantedAuthority(student.getRole().name()));
		} else {
			authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
		}
		return authorities;
	}

	@Override
	public String getName() {
		if (provider != null && provider.getEmail() != null) {
			return provider.getEmail();
		}
		return null;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}
}
