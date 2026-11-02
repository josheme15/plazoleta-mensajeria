package com.plazoleta.mensajeria.application.handler;

import com.plazoleta.mensajeria.application.dto.request.SmsRequestDto;
import com.plazoleta.mensajeria.application.dto.response.SmsResponseDto;

public interface ISmsHandler {

    SmsResponseDto sendSms(SmsRequestDto smsRequestDto);
}
