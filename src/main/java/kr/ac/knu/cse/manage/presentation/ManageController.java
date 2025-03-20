package kr.ac.knu.cse.manage.presentation;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.ac.knu.cse.dues.domain.Dues;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.student.domain.Student;
import kr.ac.knu.cse.student.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ManageController {

	private final StudentRepository studentRepository;
	private final DuesRepository duesRepository;

	/**
	 * 관리자 페이지 접근 (ROLE_FINANCE 전용)
	 */
	@GetMapping("/manage")
	@PreAuthorize("hasRole('FINANCE')")
	public String showManagePage(Model model) {
		List<Student> studentList = studentRepository.findAll();
		List<Dues> duesList = duesRepository.findAll();

		model.addAttribute("students", studentList);
		model.addAttribute("duesList", duesList);

		return "manage";
	}
}
