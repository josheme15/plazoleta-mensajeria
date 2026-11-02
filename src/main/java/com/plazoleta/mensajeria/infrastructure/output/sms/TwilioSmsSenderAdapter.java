package com.plazoleta.mensajeria.infrastructure.output.sms;

import com.plazoleta.mensajeria.domain.spi.ISmsSenderPort;
import com.plazoleta.mensajeria.infrastructure.output.sms.exception.SmsDeliveryException;
import lombok.RequiredArgsConstructor;

/**
 * Proveedor real opcional (sms.provider=twilio). Twilio exige celulares en formato internacional.
 */
@RequiredArgsConstructor
public class TwilioSmsSenderAdapter implements ISmsSenderPort {

    private final TwilioClient twilioClient;
    private final String fromNumber;
    private final String defaultCountryCode;

    @Override
    public void send(String phoneNumber, String message) {
        try {
            twilioClient.sendMessage(toInternationalFormat(phoneNumber), fromNumber, message);
        } catch (RuntimeException e) {
            throw new SmsDeliveryException(e);
        }
    }

    private String toInternationalFormat(String phoneNumber) {
        return phoneNumber.startsWith("+") ? phoneNumber : defaultCountryCode + phoneNumber;
    }
}
