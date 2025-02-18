package kr.ac.knu.cse.dues.domain;

import kr.ac.knu.cse.student.domain.Major;
import kr.ac.knu.cse.student.domain.Student;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class CsvDuesReader {

    private static final String DELIM = ",";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy. M. d a h:m:s", Locale.KOREAN
    );

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

                final Student student = Student.builder()
                    .name(name)
                    .studentNumber(studentNumber)
                    .major(major)
                    .build();

                final Dues dues = Dues.builder()
                    .student(student)
                    .depositorName(depositorName)
                    .amount(100000)
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
