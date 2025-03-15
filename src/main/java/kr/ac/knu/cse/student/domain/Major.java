package kr.ac.knu.cse.student.domain;

public enum Major {
	PLATFORM,
	AI,
	GLOBAL;

	public static Major from(final String major) {
		return switch (major) {
			case "플랫폼SW&데이터과학전공(구 심화컴퓨터공학전공)" -> PLATFORM;
			case "인공지능컴퓨팅전공" -> AI;
			case "글로벌SW융합전공" -> GLOBAL;
			default -> throw new IllegalArgumentException("Invalid major: " + major);
		};
	}
}
