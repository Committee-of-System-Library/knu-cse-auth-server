package kr.ac.knu.cse.domain.student;

public enum Grade {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    OTHERS;

    public Grade promote() {
        if (this == FIRST) {
            return SECOND;
        }
        if (this == SECOND) {
            return THIRD;
        }
        if (this == THIRD) {
            return FOURTH;
        }

        return OTHERS;
    }
}
