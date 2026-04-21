package kr.ac.knu.cse.application.dto;

import kr.ac.knu.cse.domain.student.Gender;
import kr.ac.knu.cse.domain.student.UserType;

public record SignupCommand(
        String providerName,
        String providerKey,
        String email,
        String studentNumber,
        String name,
        String major,
        Gender gender,
        UserType userType
) { }
