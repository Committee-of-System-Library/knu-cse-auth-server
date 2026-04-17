package kr.ac.knu.cse.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import kr.ac.knu.cse.domain.snack.SnackEvent;
import kr.ac.knu.cse.domain.snack.SnackHandout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class SnackXlsxExporter {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[] HEADERS = {"학번", "이름", "전공", "받은 시간"};

    public byte[] export(SnackEvent event, List<SnackHandout> handouts) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(event.getName());

            CellStyle headerStyle = createHeaderStyle(workbook);

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (SnackHandout handout : handouts) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(handout.getStudentNumber());
                row.createCell(1).setCellValue(handout.getName());
                row.createCell(2).setCellValue(handout.getMajor() != null ? handout.getMajor() : "");
                row.createCell(3).setCellValue(handout.getReceivedAt().format(TIME_FORMAT));
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Workaround for autoSizeColumn underestimating Korean width
            for (int i = 0; i < HEADERS.length; i++) {
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(currentWidth + 1024, 65280));
            }

            // Freeze header row
            sheet.createFreezePane(0, 1);

            // Suppress autoFilter on empty sheets to avoid POI errors
            if (rowIdx > 1) {
                sheet.setAutoFilter(new CellRangeAddress(0, rowIdx - 1, 0, HEADERS.length - 1));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("XLSX export failed", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
