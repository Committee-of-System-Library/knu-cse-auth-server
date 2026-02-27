package kr.ac.knu.cse.application.dto;

import kr.ac.knu.cse.global.exception.provisioning.InvalidStudentIdException;

public record OAuthLoginResult(
        Long studentId,
        boolean isNewUser
) {
    public static OAuthLoginResult existingUser(Long studentId) {
        if (studentId == null) {
            throw new InvalidStudentIdException();
        }
        return new OAuthLoginResult(studentId, false);
    }

    public static OAuthLoginResult newUser() {
        return new OAuthLoginResult(null, true);
    }
}
