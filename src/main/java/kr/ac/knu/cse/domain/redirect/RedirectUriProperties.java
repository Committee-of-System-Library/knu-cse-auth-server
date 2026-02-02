package kr.ac.knu.cse.domain.redirect;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sso")
public record RedirectUriProperties(Set<String> redirectAllowlist) {}
