package kr.ac.knu.cse.global.exception.provisioning;

import kr.ac.knu.cse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProvisioningErrorCode implements ErrorCode {

    PROVIDER_HAS_NO_STUDENT("PROVISION_001", "Provider와 연결된 Student가 존재하지 않습니다."),
    INVALID_ROLE("PROVISION_002", "잘못된 권한입니다."),
    INVALID_STUDENT_NUMBER("PROVISION_003", "잘못된 학번입니다."),
    STUDENT_NOT_FOUND("PROVISION_004", "존재하지 않는 학생입니다."),
    INVALID_STUDENT_ID("PROVISION_005", "잘못된 학생 id 입니다.");

    private final String code;
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
