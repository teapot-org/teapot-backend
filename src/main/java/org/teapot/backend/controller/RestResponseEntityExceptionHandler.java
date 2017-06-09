package org.teapot.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.teapot.backend.controller.exception.ResourceNotFoundException;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handleError404() {
        return new ResponseEntity<>(NOT_FOUND);
    }
}
