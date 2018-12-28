package br.com.becommerce.infrastructure.exception;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.becommerce.core.exception.BadArgumentException;
import br.com.becommerce.core.exception.ExceptionDTO;
import br.com.becommerce.core.exception.NotFoundException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleConflict(NotFoundException exception, WebRequest request) {
        return handleExceptionInternal(
                exception,
                buildDTO(exception),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(BadArgumentException.class)
    protected ResponseEntity<Object> handleConflict(BadArgumentException exception, WebRequest request) {
        return handleExceptionInternal(
                exception,
                buildDTO(exception),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<Object> handleConflict(IllegalStateException exception, WebRequest request) {
        return handleExceptionInternal(
                exception,
                buildDTO(exception),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return handleExceptionInternal(
                exception,
                buildDTO(exception),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    private ExceptionDTO buildDTO(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setType(exception.getClass().getSimpleName());
        exceptionDTO.setErrors(Collections.emptyList());
        exceptionDTO.setStackTrace(ExceptionUtils.getStackTrace(exception));
        exceptionDTO.setMessage(exception.getMessage());

        return exceptionDTO;
    }

    private ExceptionDTO buildDTO(MethodArgumentNotValidException exception) {
        List<String> errors = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::parseError)
                .collect(Collectors.toList());

        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setType(exception.getClass().getSimpleName());
        exceptionDTO.setErrors(errors);
        exceptionDTO.setStackTrace(ExceptionUtils.getStackTrace(exception));
        exceptionDTO.setMessage(
                errors.stream().reduce((current, error) -> String.format("%s;%s", current, error)).orElse(""));

        return exceptionDTO;
    }

    private String parseError(ObjectError objectError) {
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;

            return String.format("%s:%s:%s:%s",
                    fieldError.getObjectName(),
                    fieldError.getField(),
                    fieldError.getDefaultMessage(),
                    fieldError.getRejectedValue());
        } else {
            return String.format("%s:%s", objectError.getObjectName(), objectError.getDefaultMessage());
        }
    }
}
