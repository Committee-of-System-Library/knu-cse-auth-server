package kr.ac.knu.cse.global.exception.snack;

import kr.ac.knu.cse.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SnackErrorCode implements ErrorCode {

    SNACK_EVENT_NOT_FOUND("SNACK_001", "야식 이벤트를 찾을 수 없습니다."),
    SNACK_EVENT_ALREADY_CLOSED("SNACK_002", "이미 종료된 이벤트입니다."),
    SNACK_EVENT_NOT_OPEN("SNACK_003", "현재 진행 중인 이벤트가 아닙니다."),
    LEDGER_LOOKUP_FAILED("SNACK_004", "납부 정보 조회에 실패했습니다.");

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
