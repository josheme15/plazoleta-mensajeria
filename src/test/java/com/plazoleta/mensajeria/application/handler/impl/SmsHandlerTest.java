package com.plazoleta.mensajeria.application.handler.impl;

import com.plazoleta.mensajeria.application.dto.request.SmsRequestDto;
import com.plazoleta.mensajeria.application.dto.response.SmsResponseDto;
import com.plazoleta.mensajeria.domain.api.ISmsServicePort;
import com.plazoleta.mensajeria.domain.model.SmsMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmsHandlerTest {

    @Mock
    private ISmsServicePort smsServicePort;

    @InjectMocks
    private SmsHandler smsHandler;

    @Test
    void sendSmsMapsRequestToDomainAndReturnsSentStatus() {
        SmsResponseDto response = smsHandler.sendSms(new SmsRequestDto("+573005698325", "Hola"));

        ArgumentCaptor<SmsMessage> captor = ArgumentCaptor.forClass(SmsMessage.class);
        verify(smsServicePort).sendSms(captor.capture());
        assertThat(captor.getValue().getPhoneNumber()).isEqualTo("+573005698325");
        assertThat(captor.getValue().getMessage()).isEqualTo("Hola");
        assertThat(response.getStatus()).isEqualTo("ENVIADO");
        assertThat(response.getPhoneNumber()).isEqualTo("+573005698325");
    }
}
