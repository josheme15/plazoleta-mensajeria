package com.plazoleta.mensajeria.infrastructure.output.sms.exception;

public class SmsDeliveryException extends RuntimeException {

    public SmsDeliveryException(Throwable cause) {
        super("No fue posible enviar el SMS: el proveedor de mensajeria no esta disponible", cause);
    }
}
