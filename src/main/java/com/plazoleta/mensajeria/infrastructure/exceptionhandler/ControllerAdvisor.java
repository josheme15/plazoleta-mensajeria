package com.plazoleta.mensajeria.infrastructure.exceptionhandler;

import com.plazoleta.mensajeria.domain.exception.DomainException;
import com.plazoleta.mensajeria.infrastructure.output.sms.exception.SmsDeliveryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SmsDeliveryException.class)
    public ResponseEntity<ErrorResponse> handleSmsDelivery(SmsDeliveryException ex) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Error de validacion en la solicitud", details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud es invalido o tiene un formato incorrecto");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, message));
    }
}
