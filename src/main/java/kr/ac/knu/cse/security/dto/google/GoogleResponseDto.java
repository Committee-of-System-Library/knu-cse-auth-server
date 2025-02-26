package kr.ac.knu.cse.security.dto.google;

import java.util.Map;

import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.security.dto.Oauth2ResponseDto;

public class GoogleResponseDto implements Oauth2ResponseDto {

    private final Map<String, Object> attributes;

    public GoogleResponseDto(Map<String, Object> attribute) {
        this.attributes = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public Provider toEntity() {
        return Provider.builder()
            .email(getEmail())
            .providerName(getProvider())
            .providerKey(getProviderId())
            .build();
    }
}
