package com.plazoleta.mensajeria.infrastructure.output.sms;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Implementacion con el SDK oficial de Twilio. Se autentica con una API Key propia del proyecto.
 */
public class TwilioSdkClient implements TwilioClient {

    private final TwilioRestClient restClient;

    public TwilioSdkClient(String accountSid, String apiKeySid, String apiKeySecret) {
        this.restClient = new TwilioRestClient.Builder(apiKeySid, apiKeySecret).accountSid(accountSid).build();
    }

    @Override
    public void sendMessage(String to, String from, String body) {
        Message.creator(new PhoneNumber(to), new PhoneNumber(from), body).create(restClient);
    }
}
