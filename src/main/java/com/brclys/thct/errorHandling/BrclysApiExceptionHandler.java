package com.brclys.thct.errorHandling;

import com.brclys.thct.delegate.exception.BrclysApiErrorType;
import com.brclys.thct.delegate.exception.BrclysApiException;
import com.brclys.thct.openApiGenSrc.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class BrclysApiExceptionHandler extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(BrclysApiExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        String error = "Malformed JSON request";
        return buildResponseEntity(new BrclysRestApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(BrclysApiException.class)
    protected ResponseEntity<Object> handleCustomException(
            BrclysApiException ex) {
        BrclysApiErrorType type = ex.getBarclysApiErrorType();
        return switch (type) {
            case BAD_REQUEST ->
                    buildResponseEntity(new BrclysRestApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
            case NOT_FOUND -> buildResponseEntity(new BrclysRestApiError(NOT_FOUND, ex.getMessage(), ex));
            case FORBIDDEN -> buildResponseEntity(new BrclysRestApiError(HttpStatus.FORBIDDEN, ex.getMessage(), ex));
            default ->
                    buildResponseEntity(new BrclysRestApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        };

    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(
            RuntimeException ex) {
        BrclysRestApiError brclysRestApiError = new BrclysRestApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        String supportErrorUuid = java.util.UUID.randomUUID().toString();
        logger.error("Runtime Exception Occurred: {}; Support Error Message Id: {}", supportErrorUuid, ex.getMessage(),
                ex);
        brclysRestApiError.setMessage("Error Occurred. If this problem persists, please contact support with error message id: " + supportErrorUuid);
        return buildResponseEntity(brclysRestApiError);
    }


    private ResponseEntity<Object> buildResponseEntity(BrclysRestApiError brclysRestApiError) {
        ErrorResponse errorResponse = new ErrorResponse(brclysRestApiError.getMessage());
        return new ResponseEntity<>(errorResponse, brclysRestApiError.getStatus());
    }

}