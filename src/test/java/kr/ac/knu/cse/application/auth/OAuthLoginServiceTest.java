package kr.ac.knu.cse.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

//    @Mock
//    private ProviderRepository providerRepository;
//    @Mock
//    private StudentRepository studentRepository;
//    @InjectMocks
//    private OAuthLoginService oAuthLoginService;
//
//    @DisplayName("신규 로그인 시, 임시 학번을 가진 Student와 Provider를 생성하고 id를 반환한다.")
//    @Test
//    void login_createsStudentWithTempNumber() {
//        // set up for Provider
//        given(providerRepository.findByProviderNameAndProviderKey(any(), any()))
//                .willReturn(Optional.empty());
//
//        //set up for userInfo
//        OAuthUserInfo userInfo = OAuthUserInfo.of(
//                "GOOGLE",
//                "provider-key",
//                "user@knu.ac.kr",
//                "홍길동"
//        );
//
//        // when
//        OAuthLoginResult result = oAuthLoginService.login(userInfo);
//
//
//        //then
//        System.out.println();
////        assertThat(savedStudent.getStudentNumber())
////                .startsWith("TEMP")
////                .hasSize(15);
//
////        assertThat(result.studentId()).isEqualTo(savedStudent.getId());
//    }
//
//    @DisplayName("임시 학번이 충돌하면 재시도 후 성공한다")
//    @Test
//    void retryWhenStudentNumberDuplicated() {
//        // given
//
//        //set up for Provider
//        OAuthUserInfo userInfo = OAuthUserInfo.of(
//                "GOOGLE",
//                "provider-key",
//                "user@knu.ac.kr",
//                "홍길동"
//        );
//
//        given(providerRepository.findByProviderNameAndProviderKey(any(), any()))
//                .willReturn(Optional.empty());
//
//        //set up for Student
//        Student student = Student.of("홍길동", "TEMP00000000002");
//        student.assignTempIdForTest();
//
//        given(studentRepository.save(any(Student.class)))
//                .willThrow(DataIntegrityViolationException.class)
//                .willReturn(student);
//
//        given(studentRepository.findById(anyLong()))
//                .willReturn(Optional.of(student));
//
//        // when
//        OAuthLoginResult result = oAuthLoginService.login(userInfo);
//
//        // then
//        assertThat(result.studentId()).isEqualTo(1L);
//    }
}