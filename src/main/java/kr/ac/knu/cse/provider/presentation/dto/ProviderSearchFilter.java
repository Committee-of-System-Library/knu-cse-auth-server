package kr.ac.knu.cse.provider.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderSearchFilter {
    private String sortBy = "email";
    private String direction = "asc";

    private String searchColumn;
    private String searchKeyword;
}
