package com.example.saratogapizza.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDetail> handleAuthException(AuthException ae, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setTimestamp(LocalDateTime.now());

        errorDetail.setError(ae.getMessage());

        errorDetail.setDetails(request.getDescription(false));

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(AddressException.class)
    public ResponseEntity<ErrorDetail> handleAddressException(AddressException ae, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setTimestamp(LocalDateTime.now());

        errorDetail.setError(ae.getMessage());

        errorDetail.setDetails(request.getDescription(false));

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(VerifyException.class)
    public ResponseEntity<ErrorDetail> handleVerifyException(VerifyException ae, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setTimestamp(LocalDateTime.now());

        errorDetail.setError(ae.getMessage());

        errorDetail.setDetails(request.getDescription(false));

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);

    }
}
