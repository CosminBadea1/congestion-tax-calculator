package com.assignment.congestion.tax.calculator.adapters.rest.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorDto(INTERNAL_SERVER_ERROR.getReasonPhrase(), "The server encountered an error. Please try again later!");
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        String violations = extractViolationsFromException(methodArgumentNotValidException);
        log.error(methodArgumentNotValidException.getMessage(), methodArgumentNotValidException);
        return new ErrorDto(BAD_REQUEST.getReasonPhrase(), violations);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ErrorDto handleConstraintViolationException(ConstraintViolationException exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorDto(BAD_REQUEST.getReasonPhrase(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ErrorDto handleArgumentException(Exception argumentException) {
        log.error(argumentException.getMessage(), argumentException);
        return new ErrorDto(BAD_REQUEST.getReasonPhrase(), argumentException.getMessage());
    }

    private String extractViolationsFromException(BindException bindingException) {
        return bindingException.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> "%s %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
                .sorted()
                .collect(joining(" -- "));
    }
}
