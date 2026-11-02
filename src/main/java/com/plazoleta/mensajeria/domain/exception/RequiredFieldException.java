package com.plazoleta.mensajeria.domain.exception;

public class RequiredFieldException extends DomainException {

    public RequiredFieldException(String fieldName) {
        super("El campo " + fieldName + " es obligatorio");
    }
}
