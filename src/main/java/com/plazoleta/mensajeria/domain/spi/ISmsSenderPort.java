package com.plazoleta.mensajeria.domain.spi;

/**
 * Puerto hacia el proveedor que envia el SMS (simulado o Twilio). El dominio no sabe cual se usa.
 */
public interface ISmsSenderPort {

    void send(String phoneNumber, String message);
}
