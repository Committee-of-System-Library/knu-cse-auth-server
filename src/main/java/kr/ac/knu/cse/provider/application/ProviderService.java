package kr.ac.knu.cse.provider.application;

import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.exception.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.provider.presentation.dto.PatchUpdateProviderReq;
import kr.ac.knu.cse.provider.presentation.dto.PostCreateProviderReq;
import kr.ac.knu.cse.security.dto.Oauth2ResponseDto;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.exception.StudentNotFoundException;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Provider loadOrSaveProvider(Oauth2ResponseDto oauth2ResponseDto) {
        log.info("Provider 조회 또는 생성 - 이메일: {}", oauth2ResponseDto.getEmail());
        return providerRepository.findByEmail(oauth2ResponseDto.getEmail())
                .orElseGet(() -> {
                    log.info("새로운 Provider 생성 - 이메일: {}", oauth2ResponseDto.getEmail());
                    return providerRepository.save(oauth2ResponseDto.toEntity());
                });
    }

    @Transactional
    public void connectStudent(String email, Student student) {
        log.info("학생 정보 연동 - 이메일: {}, 학생 ID: {}", email, student.getId());
        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Provider를 찾을 수 없습니다: {}", email);
                    return new ProviderNotFoundException();
                });
        provider.connectStudent(student);
        log.info("학생 정보 연동 완료");
    }

    @Transactional
    public Long createProvider(PostCreateProviderReq req) {
        log.info("Provider 생성 - 이메일: {}", req.email());
        Provider provider = Provider.builder()
                .email(req.email())
                .providerName(req.providerName())
                .providerKey(req.providerKey())
                .build();

        if (req.studentId() != null) {
            log.info("학생 정보 연동 - 학생 ID: {}", req.studentId());
            Student student = studentRepository.findById(req.studentId())
                    .orElseThrow(() -> {
                        log.error("학생을 찾을 수 없습니다: {}", req.studentId());
                        return new StudentNotFoundException();
                    });
            provider.connectStudent(student);
        }
        providerRepository.save(provider);
        log.info("Provider 생성 완료 - ID: {}", provider.getId());
        return provider.getId();
    }

    @Transactional
    public void updateProvider(Long providerId, PatchUpdateProviderReq req) {
        log.info("Provider 수정 - ID: {}", providerId);
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> {
                    log.error("Provider를 찾을 수 없습니다: {}", providerId);
                    return new ProviderNotFoundException();
                });

        provider.updateProviderInfo(
                req.email(),
                req.providerName(),
                req.providerKey()
        );

        if (req.studentId() != null) {
            log.info("학생 정보 연동 - 학생 ID: {}", req.studentId());
            Student student = studentRepository.findById(req.studentId())
                    .orElseThrow(() -> {
                        log.error("학생을 찾을 수 없습니다: {}", req.studentId());
                        return new StudentNotFoundException();
                    });
            provider.connectStudent(student);
        } else {
            log.info("학생 정보 연동 해제");
            provider.disconnectStudent();
        }
        log.info("Provider 수정 완료 - ID: {}", providerId);
    }

    @Transactional
    public void deleteProvider(Long providerId) {
        log.info("Provider 삭제 - ID: {}", providerId);
        if (!providerRepository.existsById(providerId)) {
            log.error("Provider를 찾을 수 없습니다: {}", providerId);
            throw new ProviderNotFoundException();
        }
        providerRepository.deleteById(providerId);
        log.info("Provider 삭제 완료 - ID: {}", providerId);
    }

    @Transactional
    public void deleteProviders(List<Long> providerIds) {
        log.info("Provider 다중 삭제 - ID 목록: {}", providerIds);
        providerRepository.deleteAllById(providerIds);
        log.info("Provider 다중 삭제 완료");
    }
}
