package kr.ac.knu.cse.provider.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.excpetion.ProviderNotFoundException;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.security.dto.Oauth2ResponseDto;
import kr.ac.knu.cse.student.domain.Student;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderService {
	private final ProviderRepository providerRepository;

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
}
