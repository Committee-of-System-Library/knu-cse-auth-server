package kr.ac.knu.cse.global.exception.snack;

import kr.ac.knu.cse.global.exception.BusinessException;

public class LedgerLookupFailedException extends BusinessException {
    public LedgerLookupFailedException() {
        super(SnackErrorCode.LEDGER_LOOKUP_FAILED);
    }
}
