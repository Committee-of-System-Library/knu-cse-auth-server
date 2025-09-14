package kr.ac.knu.cse.client.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateClientRequest {

	@NotBlank(message = "클라이언트명은 필수입니다.")
	@Size(max = 100, message = "클라이언트명은 100자를 초과할 수 없습니다.")
	private String clientName;

	@Size(max = 500, message = "클라이언트 설명은 500자를 초과할 수 없습니다.")
	private String clientDescription;

	@NotEmpty(message = "허용 도메인은 최소 1개 이상 설정해야 합니다.")
	private List<String> allowedDomains;
}
