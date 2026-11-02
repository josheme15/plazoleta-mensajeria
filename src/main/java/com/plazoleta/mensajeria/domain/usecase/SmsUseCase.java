package com.plazoleta.mensajeria.domain.usecase;

import com.plazoleta.mensajeria.domain.api.ISmsServicePort;
import com.plazoleta.mensajeria.domain.exception.InvalidPhoneException;
import com.plazoleta.mensajeria.domain.exception.RequiredFieldException;
import com.plazoleta.mensajeria.domain.model.SmsMessage;
import com.plazoleta.mensajeria.domain.spi.ISmsSenderPort;

import java.util.regex.Pattern;

public class SmsUseCase implements ISmsServicePort {

    // Mismo formato de celular que en usuarios: maximo 13 caracteres, '+' opcional al inicio.
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+\\d{1,12}|\\d{1,13})$");

    private final ISmsSenderPort smsSenderPort;

    public SmsUseCase(ISmsSenderPort smsSenderPort) {
        this.smsSenderPort = smsSenderPort;
    }

    @Override
    public void sendSms(SmsMessage smsMessage) {
        String phone = smsMessage.getPhoneNumber();
        String message = smsMessage.getMessage();
        if (phone == null || phone.isBlank()) {
            throw new RequiredFieldException("celular");
        }
        if (message == null || message.isBlank()) {
            throw new RequiredFieldException("mensaje");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidPhoneException();
        }
        smsSenderPort.send(phone, message);
    }
}
