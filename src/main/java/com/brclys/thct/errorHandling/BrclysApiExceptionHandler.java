package com.brclys.thct.errorHandling;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class BrclysApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        String error = "Malformed JSON request";
        return buildResponseEntity(new BrclysRestApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleBadRequest(
            IllegalArgumentException ex) {
        BrclysRestApiError brclysRestApiError = new BrclysRestApiError(HttpStatus.BAD_REQUEST);
        brclysRestApiError.setMessage(ex.getMessage());
        return buildResponseEntity(brclysRestApiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        BrclysRestApiError brclysRestApiError = new BrclysRestApiError(NOT_FOUND);
        brclysRestApiError.setMessage(ex.getMessage());
        return buildResponseEntity(brclysRestApiError);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(
            RuntimeException ex) {
        BrclysRestApiError brclysRestApiError = new BrclysRestApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        brclysRestApiError.setMessage(ex.getMessage());
        return buildResponseEntity(brclysRestApiError);
    }


    private ResponseEntity<Object> buildResponseEntity(BrclysRestApiError brclysRestApiError) {
        return new ResponseEntity<>(brclysRestApiError, brclysRestApiError.getStatus());
    }

}