package kr.ac.knu.cse.provider.application;

import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.exception.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.provider.presentation.dto.PatchUpdateProviderReq;
import kr.ac.knu.cse.provider.presentation.dto.PostCreateProviderReq;
import kr.ac.knu.cse.security.dto.Oauth2ResponseDto;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Provider loadOrSaveProvider(Oauth2ResponseDto oauth2ResponseDto) {
        return providerRepository.findByEmail(oauth2ResponseDto.getEmail())
                .orElseGet(() -> providerRepository.save(oauth2ResponseDto.toEntity()));
    }

    @Transactional
    public void connectStudent(String email, Student student) {
        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(ProviderNotFoundException::new);
        provider.connectStudent(student);
    }

    @Transactional
    public Long createProvider(PostCreateProviderReq req) {
        Provider provider = Provider.builder()
                .email(req.email())
                .providerName(req.providerName())
                .providerKey(req.providerKey())
                .build();

        if (req.studentId() != null) {
            Student student = studentRepository.findById(req.studentId())
                    .orElseThrow(() -> new RuntimeException("STUDENT_NOT_FOUND"));
            provider.connectStudent(student);
        }
        providerRepository.save(provider);
        return provider.getId();
    }

    @Transactional
    public void updateProvider(Long providerId, PatchUpdateProviderReq req) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(ProviderNotFoundException::new);

        provider.updateProviderInfo(
                req.email(),
                req.providerName(),
                req.providerKey()
        );

        if (req.studentId() != null) {
            Student student = studentRepository.findById(req.studentId())
                    .orElseThrow(() -> new RuntimeException("STUDENT_NOT_FOUND"));
            provider.connectStudent(student);
        } else {
            provider.disconnectStudent();
        }
    }

    @Transactional
    public void deleteProvider(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new ProviderNotFoundException();
        }
        providerRepository.deleteById(providerId);
    }

    @Transactional
    public void deleteProviders(List<Long> providerIds) {
        providerRepository.deleteAllById(providerIds);
    }
}
