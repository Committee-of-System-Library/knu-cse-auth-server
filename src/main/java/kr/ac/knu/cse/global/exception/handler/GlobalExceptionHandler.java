package kr.ac.knu.cse.global.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.knu.cse.global.exception.BaseExceptionHandler;
import kr.ac.knu.cse.global.exception.support.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler<GlobalException> {

    @ExceptionHandler(GlobalException.class)
    public Object handleCustomException(GlobalException exception, HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return handleException(exception, exception.getHttpStatus(), exception.getErrorMsg());
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", exception.getErrorMsg());
            return mav;
        }
    }
}
