package kr.ac.knu.cse.provider.excpetion;

import kr.ac.knu.cse.global.exception.support.business.NotFoundException;

public class ProviderNotFoundException extends NotFoundException {
	private static final String errorMsg = "PROVIDER_NOT_FOUND";

	public ProviderNotFoundException() {
		super(errorMsg);
	}
}
