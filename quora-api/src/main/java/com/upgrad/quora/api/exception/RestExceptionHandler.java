package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import com.upgrad.quora.service.common.UnexpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException afe, WebRequest webRequest) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(afe.getCode()).message(afe.getErrorMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuestionException(AuthenticationFailedException exception, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuestionException(SignOutRestrictedException exception, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuestionException(SignUpRestrictedException exception, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(UserNotFoundException unf, WebRequest webRequest) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(unf.getCode()).message(unf.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuestionException(InvalidQuestionException exception, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuestionException(AnswerNotFoundException exception, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(UnexpectedException exception, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse().code(exception.getErrorCode().getCode()).message(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
