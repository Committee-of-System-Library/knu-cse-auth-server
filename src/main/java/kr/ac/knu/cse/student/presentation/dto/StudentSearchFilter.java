package kr.ac.knu.cse.student.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSearchFilter {
	private String sortBy = "studentNumber";
	private String direction = "asc";
}
