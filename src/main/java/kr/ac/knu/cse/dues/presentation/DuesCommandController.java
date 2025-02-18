package kr.ac.knu.cse.dues.presentation;

import kr.ac.knu.cse.dues.application.DuesCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dues")
public class DuesCommandController {

    private final DuesCommandService duesCommandService;

    @PostMapping
    public void submitDues(@RequestParam("file") final MultipartFile file) {
        try (final InputStream in = file.getInputStream()) {
            duesCommandService.submitAll(in);
        } catch (final IOException exception) {
            throw new IllegalArgumentException("파일을 읽는 중 오류가 발생했습니다.");
        }
    }
}
