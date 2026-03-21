package kr.ac.knu.cse.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AppUpdateRequest(
        @NotBlank String appName,
        String description,
        @NotEmpty List<String> redirectUris,
        String homepageUrl
) {
}
