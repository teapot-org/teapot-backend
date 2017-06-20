package org.teapot.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ConflictException;
import org.teapot.backend.controller.exception.ForbiddenException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;

import static org.springframework.http.HttpStatus.*;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handleError404() {
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleError400() {
        return new ResponseEntity<>(BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleError403() {
        return new ResponseEntity<>(FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleError409() {
        return new ResponseEntity<>(CONFLICT);
    }
}
