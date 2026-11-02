package com.plazoleta.mensajeria.infrastructure.output.sms;

/**
 * Envoltorio del SDK de Twilio, para poder probar el adaptador sin enviar SMS reales.
 */
public interface TwilioClient {

    void sendMessage(String to, String from, String body);
}
