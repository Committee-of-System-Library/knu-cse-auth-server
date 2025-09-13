package kr.ac.knu.cse.token.domain;

import lombok.Getter;

@Getter
public class Token {
	private final String type;
	private final String value;
	
	public Token(String type, String value) {
		this.type = type;
		this.value = value;
	}
}
