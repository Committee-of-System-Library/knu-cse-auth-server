package kr.ac.knu.cse.manage.presentation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.provider.domain.Provider;
import kr.ac.knu.cse.provider.persistence.ProviderRepository;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ManageController {

	private final StudentRepository studentRepository;
	private final DuesRepository duesRepository;
	private final ProviderRepository providerRepository; // 추가

	@GetMapping("/manage")
	@PreAuthorize("hasRole('FINANCE')")
	public String showManagePage(Model model) {
		List<Student> studentList = studentRepository.findAll();
		List<Dues> duesList = duesRepository.findAll();
		List<Provider> providers = providerRepository.findAll();

		// 회비 납부가 등록된 학생들의 ID 목록
		List<Long> dueStudentIds = duesList.stream()
			.map(dues -> dues.getStudent().getId())
			.collect(Collectors.toList());

		model.addAttribute("students", studentList);
		model.addAttribute("duesList", duesList);
		model.addAttribute("dueStudentIds", dueStudentIds);
		model.addAttribute("providers", providers);

		return "manage";
	}
}
