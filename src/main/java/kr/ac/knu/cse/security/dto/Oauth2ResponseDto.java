package kr.ac.knu.cse.security.dto;

import kr.ac.knu.cse.provider.domain.Provider;

public interface Oauth2ResponseDto {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    Provider toEntity();
}
