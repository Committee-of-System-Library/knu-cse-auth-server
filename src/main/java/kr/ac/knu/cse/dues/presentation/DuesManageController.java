package kr.ac.knu.cse.dues.presentation;

import jakarta.validation.Valid;
import kr.ac.knu.cse.dues.application.DuesManageService;
import kr.ac.knu.cse.dues.application.mapper.DuesMapper;
import kr.ac.knu.cse.dues.persistence.DuesRepository;
import kr.ac.knu.cse.dues.presentation.dto.CreateDuesPostReq;
import kr.ac.knu.cse.dues.presentation.dto.DuesListResponse;
import kr.ac.knu.cse.dues.presentation.dto.DuesSearchFilter;
import kr.ac.knu.cse.dues.presentation.dto.UpdateDuesPatchReq;
import kr.ac.knu.cse.global.api.ApiResponse;
import kr.ac.knu.cse.global.api.ApiSuccessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dues")
@PreAuthorize("hasRole('FINANCE')")
@Slf4j
public class DuesManageController {

    private final DuesRepository duesRepository;
    private final DuesManageService duesManageService;

    @GetMapping
    public ResponseEntity<ApiSuccessResult<Page<DuesListResponse>>> getDuesList(
            @ModelAttribute DuesSearchFilter filter,
            Pageable pageable
    ) {
        log.info("학생회비 납부 목록 조회 요청 - 필터: {}, 페이지: {}", filter, pageable);
        Page<DuesListResponse> page = duesRepository.findDuesList(filter, pageable);
        log.info("학생회비 납부 목록 조회 완료 - 총 {}개", page.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, page));
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResult<?>> createDues(
            @Valid @RequestBody CreateDuesPostReq requestBody
    ) {
        log.info("학생회비 납부 정보 생성 요청 - {}", requestBody);
        Long id = duesManageService.createDues(DuesMapper.toCreateDuesDto(requestBody));
        log.info("학생회비 납부 정보 생성 완료 - ID: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Dues created with ID: " + id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResult<?>> updateDues(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDuesPatchReq requestBody
    ) {
        log.info("학생회비 납부 정보 수정 요청 - ID: {}, {}", id, requestBody);
        duesManageService.updateDues(id, DuesMapper.toUpdateDuesDto(requestBody));
        log.info("학생회비 납부 정보 수정 완료 - ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "Dues updated"));
    }

    @DeleteMapping
    public ResponseEntity<ApiSuccessResult<?>> deleteDues(@RequestParam("ids") List<Long> ids) {
        log.info("학생회비 납부 정보 삭제 요청 - ID 목록: {}", ids);
        duesManageService.deleteDues(ids);
        log.info("학생회비 납부 정보 삭제 완료");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "선택된 납부 내역이 삭제되었습니다."));
    }
}
