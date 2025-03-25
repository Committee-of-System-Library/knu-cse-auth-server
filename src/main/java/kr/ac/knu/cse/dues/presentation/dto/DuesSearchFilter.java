package kr.ac.knu.cse.dues.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuesSearchFilter {
	private String sortBy = "duesId";
	private String direction = "asc";
}
