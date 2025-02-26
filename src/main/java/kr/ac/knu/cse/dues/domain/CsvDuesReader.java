package kr.ac.knu.cse.dues.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Role;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CsvDuesReader {

    private static final String DELIM = ",";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy. M. d a h:m:s", Locale.KOREAN
    );
    private static final Integer AMOUNT_PER_SEMESTER = 22_000;

    private final StudentRepository studentRepository;

    public List<Dues> read(final InputStream in) {
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            final List<Dues> res = new ArrayList<>();

            br.readLine();
            while (br.ready()) {
                final List<String> line = List.of(br.readLine().split(DELIM));
                final LocalDateTime submittedAt = LocalDateTime.parse(line.get(0), formatter);
                final String name = line.get(1);
                final String studentNumber = line.get(2);
                final Major major = Major.from(line.get(3));
                final String depositorName = line.get(4);
                final Integer remainingSemesters = Integer.parseInt(line.get(5));

                if (studentRepository.existsByStudentNumber(studentNumber)) {
                    throw new IllegalArgumentException("이미 등록된 학번입니다.");
                }

                final Student student = Student.builder()
                    .name(name)
                    .studentNumber(studentNumber)
                    .major(major)
                    .role(Role.ROLE_STUDENT)
                    .build();

                final Dues dues = Dues.builder()
                    .student(student)
                    .depositorName(depositorName)
                    .amount(AMOUNT_PER_SEMESTER * remainingSemesters)
                    .remainingSemesters(remainingSemesters)
                    .submittedAt(submittedAt)
                    .build();

                res.add(dues);
            }

            return res;
        } catch (final IOException exception) {
            throw new IllegalArgumentException("CSV 파일 형식이 잘못되었습니다.");
        }
    }
}
