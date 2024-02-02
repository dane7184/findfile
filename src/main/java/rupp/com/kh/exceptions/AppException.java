package rupp.com.kh.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import rupp.com.kh.utils.AppData;

@ControllerAdvice
public class AppException {
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        AppData.errorMessage = e.getMessage();
        // Redirect to the error page
        return "redirect:/error-occurred";
    }
    
}
