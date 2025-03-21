package kr.ac.knu.cse.provider.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.excpetion.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.provider.presentation.dto.PatchUpdateProviderReq;
import kr.ac.knu.cse.provider.presentation.dto.PostCreateProviderReq;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderManageService {

	private final ProviderRepository providerRepository;
	private final StudentRepository studentRepository;

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
}
