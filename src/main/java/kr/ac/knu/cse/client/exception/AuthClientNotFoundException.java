package kr.ac.knu.cse.client.exception;

import kr.ac.knu.cse.global.exception.support.business.NotFoundException;

public class AuthClientNotFoundException extends NotFoundException {
    public AuthClientNotFoundException() {
        super("인증 클라이언트를 찾을 수 없습니다.");
    }
}
