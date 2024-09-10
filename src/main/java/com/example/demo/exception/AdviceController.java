package com.example.demo.exception;

import com.example.demo.config.RestError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class AdviceController {


    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public ResponseEntity<RestError> handleAuthenticationException(Exception e) {
        RestError re = new RestError("Token is expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }
}
