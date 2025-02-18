package kr.ac.knu.cse.dues.application;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.dues.domain.CsvDuesReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DuesCommandService {

    private final CsvDuesReader duesReader;
    private final DuesRepository duesRepository;

    public void submit(final InputStream in) {
        final List<Dues> dues = duesReader.read(in);
        duesRepository.saveAll(dues);
    }
}
