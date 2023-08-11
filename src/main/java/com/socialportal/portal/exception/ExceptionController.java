package com.socialportal.portal.exception;

import com.socialportal.portal.exception.user.NoRolesDataBase;
import com.socialportal.portal.exception.user.UserAlreadyExists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NoRolesDataBase.class)
    ResponseEntity<String> roleNotFoundHandler(NoRolesDataBase roleException) {
        return new ResponseEntity<>(roleException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExists.class)
    ResponseEntity<String> userAlreadyExistsHandler(UserAlreadyExists userAlreadyExists) {
        return new ResponseEntity<>(userAlreadyExists.getMessage(), HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<String> authenticationExceptionHandler(AuthenticationException authenticationException) {
        return new ResponseEntity<>(authenticationException.getMessage(), HttpStatus.FORBIDDEN);
    }

}