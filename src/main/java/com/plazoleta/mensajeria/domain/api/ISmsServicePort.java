package com.plazoleta.mensajeria.domain.api;

import com.plazoleta.mensajeria.domain.model.SmsMessage;

public interface ISmsServicePort {

    void sendSms(SmsMessage smsMessage);
}
