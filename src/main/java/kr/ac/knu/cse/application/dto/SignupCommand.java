package kr.ac.knu.cse.application.dto;

import kr.ac.knu.cse.domain.student.Gender;
import kr.ac.knu.cse.domain.student.Grade;
import kr.ac.knu.cse.domain.student.UserType;

public record SignupCommand(
        String providerName,
        String providerKey,
        String email,
        String studentNumber,
        String name,
        String major,
        Grade grade,
        Gender gender,
        UserType userType
) { }
