package kr.ac.knu.cse.qr.presentation.dto;

import java.util.List;

public record SaveQrAuthLogsReq(
        List<ScannedStudent> scannedStudents
) {
}
