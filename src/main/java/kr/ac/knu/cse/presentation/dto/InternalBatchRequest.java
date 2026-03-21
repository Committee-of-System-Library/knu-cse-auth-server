package kr.ac.knu.cse.presentation.dto;

import java.util.List;

public record InternalBatchRequest(
        List<String> studentNumbers
) {
}
