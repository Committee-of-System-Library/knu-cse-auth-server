package kr.ac.knu.cse.application.dto;

import kr.ac.knu.cse.domain.student.Grade;

public record SignupCommand(
        String providerName,
        String providerKey,
        String email,
        String studentNumber,
        String name,
        String major,
        Grade grade
) { }
