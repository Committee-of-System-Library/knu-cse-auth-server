package kr.ac.knu.cse.global.exception.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String error;
    private String message;
    private int status;

    public ErrorResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
    }

    public static ErrorResponse of(String error, String message, int status) {
        return new ErrorResponse(error, message, status);
    }
}
