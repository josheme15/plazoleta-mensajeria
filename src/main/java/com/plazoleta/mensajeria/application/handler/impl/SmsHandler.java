package com.plazoleta.mensajeria.application.handler.impl;

import com.plazoleta.mensajeria.application.dto.request.SmsRequestDto;
import com.plazoleta.mensajeria.application.dto.response.SmsResponseDto;
import com.plazoleta.mensajeria.application.handler.ISmsHandler;
import com.plazoleta.mensajeria.domain.api.ISmsServicePort;
import com.plazoleta.mensajeria.domain.model.SmsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsHandler implements ISmsHandler {

    private static final String SENT = "ENVIADO";

    private final ISmsServicePort smsServicePort;

    @Override
    public SmsResponseDto sendSms(SmsRequestDto smsRequestDto) {
        smsServicePort.sendSms(new SmsMessage(smsRequestDto.getPhoneNumber(), smsRequestDto.getMessage()));
        return new SmsResponseDto(SENT, smsRequestDto.getPhoneNumber());
    }
}
