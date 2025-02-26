package kr.ac.knu.cse.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiSuccessResult<T>(int status, T data) {
}
