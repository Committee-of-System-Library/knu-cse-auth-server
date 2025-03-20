package kr.ac.knu.cse.student.application.mapper;

import kr.ac.knu.cse.student.application.dto.SaveStudentDto;
import kr.ac.knu.cse.student.application.dto.UpdateStudentDto;
import kr.ac.knu.cse.student.presentation.dto.PatchUpdateStudentReq;
import kr.ac.knu.cse.student.presentation.dto.PostCreateStudentReq;

public class StudentMapper {

	public static SaveStudentDto toSaveStudentDto(PostCreateStudentReq req) {
		return new SaveStudentDto(req.studentNumber(), req.name(), req.major(), req.role());
	}

	public static UpdateStudentDto toUpdateStudentDto(PatchUpdateStudentReq req) {
		return new UpdateStudentDto(req.studentNumber(), req.name(), req.major(), req.role());
	}
}
