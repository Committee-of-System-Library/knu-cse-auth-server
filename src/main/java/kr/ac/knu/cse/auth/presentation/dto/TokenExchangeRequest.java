package kr.ac.knu.cse.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenExchangeRequest {
	
	@NotBlank(message = "Authorization code는 필수입니다")
	private String code;
	
	@NotBlank(message = "Redirect URI는 필수입니다")
	private String redirectUrl;
	
	public TokenExchangeRequest(String code, String redirectUrl) {
		this.code = code;
		this.redirectUrl = redirectUrl;
	}
}
