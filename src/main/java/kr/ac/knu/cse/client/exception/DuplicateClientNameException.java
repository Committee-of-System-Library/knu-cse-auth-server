package kr.ac.knu.cse.client.exception;

import kr.ac.knu.cse.global.exception.support.business.DuplicatedException;

public class DuplicateClientNameException extends DuplicatedException {
    public DuplicateClientNameException() {
        super("이미 존재하는 클라이언트명입니다.");
    }
}
