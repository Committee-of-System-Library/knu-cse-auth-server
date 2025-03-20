package kr.ac.knu.cse.dues.application.mapper;

import kr.ac.knu.cse.dues.application.dto.CreateDuesDto;
import kr.ac.knu.cse.dues.application.dto.UpdateDuesDto;
import kr.ac.knu.cse.dues.presentation.dto.CreateDuesPostReq;
import kr.ac.knu.cse.dues.presentation.dto.UpdateDuesPatchReq;

public class DuesMapper {

	public static CreateDuesDto toCreateDuesDto(CreateDuesPostReq req) {
		return new CreateDuesDto(
			req.studentId(),
			req.depositorName(),
			req.amount(),
			req.remainingSemesters(),
			req.submittedAt()
		);
	}

	public static UpdateDuesDto toUpdateDuesDto(UpdateDuesPatchReq req) {
		return new UpdateDuesDto(
			req.depositorName(),
			req.amount(),
			req.remainingSemesters()
		);
	}
}
