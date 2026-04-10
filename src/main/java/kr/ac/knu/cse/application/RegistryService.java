package kr.ac.knu.cse.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import kr.ac.knu.cse.application.dto.RegistryUploadResult;
import kr.ac.knu.cse.domain.registry.CseStudentRegistry;
import kr.ac.knu.cse.domain.registry.CseStudentRegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistryService {

    private final CseStudentRegistryRepository registryRepository;

    @Transactional
    public RegistryUploadResult uploadCsv(InputStream csvFile) {
        int totalRows = 0;
        int insertedCount = 0;
        int updatedCount = 0;
        int errorCount = 0;

        try (CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setHeader("학번", "이름", "전공", "학년", "학적상태")
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build()
                .parse(new InputStreamReader(csvFile, StandardCharsets.UTF_8))) {

            for (CSVRecord record : parser) {
                totalRows++;
                try {
                    String studentNumber = record.get("학번");
                    String name = record.get("이름");
                    String major = record.get("전공");
                    String gradeStr = record.get("학년");
                    String enrollmentStatus = record.isMapped("학적상태") ? record.get("학적상태") : null;

                    Integer grade = parseGrade(gradeStr);

                    Optional<CseStudentRegistry> existing =
                            registryRepository.findByStudentNumber(studentNumber);

                    if (existing.isPresent()) {
                        existing.get().update(name, major, grade, enrollmentStatus);
                        updatedCount++;
                    } else {
                        CseStudentRegistry registry = CseStudentRegistry.of(
                                studentNumber, name, major, grade, enrollmentStatus, false
                        );
                        registryRepository.save(registry);
                        insertedCount++;
                    }
                } catch (Exception e) {
                    log.warn("CSV row {} 처리 실패: {}", totalRows, e.getMessage());
                    errorCount++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 파싱 실패", e);
        }

        return new RegistryUploadResult(totalRows, insertedCount, updatedCount, errorCount);
    }

    @Transactional(readOnly = true)
    public List<CseStudentRegistry> findAll() {
        return registryRepository.findAll();
    }

    @Transactional
    public CseStudentRegistry addManually(
            String studentNumber,
            String name,
            String major,
            Integer grade,
            String enrollmentStatus
    ) {
        Optional<CseStudentRegistry> existing =
                registryRepository.findByStudentNumber(studentNumber);

        if (existing.isPresent()) {
            existing.get().update(name, major, grade, enrollmentStatus);
            return existing.get();
        }

        CseStudentRegistry registry = CseStudentRegistry.of(
                studentNumber, name, major, grade, enrollmentStatus, true
        );
        return registryRepository.save(registry);
    }

    @Transactional
    public CseStudentRegistry changeEnrollmentStatus(String studentNumber, String enrollmentStatus) {
        CseStudentRegistry registry = registryRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다: " + studentNumber));
        registry.updateEnrollmentStatus(enrollmentStatus);
        return registry;
    }

    @Transactional
    public void delete(String studentNumber) {
        registryRepository.findByStudentNumber(studentNumber)
                .ifPresent(registryRepository::delete);
    }

    private Integer parseGrade(String gradeStr) {
        if (gradeStr == null || gradeStr.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(gradeStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
