package kr.ac.knu.cse.qr.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QrAuthLogSearchFilter {
    private String sortBy = "scanDate";
    private String direction = "desc";

    private String searchColumn;
    private String searchKeyword;
}
