package kr.ac.knu.cse.presentation.dto;

import kr.ac.knu.cse.domain.student.Gender;
import kr.ac.knu.cse.domain.student.UserType;

public record SignupRequest(
        String studentNumber,
        String major,
        Gender gender,
        UserType userType
) {
}
