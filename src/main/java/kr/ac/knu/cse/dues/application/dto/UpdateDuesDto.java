package kr.ac.knu.cse.dues.application.dto;

public record UpdateDuesDto(
    String depositorName,
    Integer amount,
    Integer remainingSemesters
) {}
