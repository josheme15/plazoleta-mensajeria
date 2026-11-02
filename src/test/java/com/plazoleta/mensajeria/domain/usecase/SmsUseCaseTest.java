package com.plazoleta.mensajeria.domain.usecase;

import com.plazoleta.mensajeria.domain.exception.InvalidPhoneException;
import com.plazoleta.mensajeria.domain.exception.RequiredFieldException;
import com.plazoleta.mensajeria.domain.model.SmsMessage;
import com.plazoleta.mensajeria.domain.spi.ISmsSenderPort;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmsUseCaseTest {

    private static final String MESSAGE = "Tu pedido #7 esta listo. Presenta este PIN para reclamarlo: 482913";

    @Mock
    private ISmsSenderPort smsSenderPort;

    @InjectMocks
    private SmsUseCase smsUseCase;

    @ParameterizedTest
    @ValueSource(strings = {"+573005698325", "3005698325", "1234567890123"})
    void sendSmsDelegatesToSenderWhenDataIsValid(String phone) {
        smsUseCase.sendSms(new SmsMessage(phone, MESSAGE));

        verify(smsSenderPort).send(phone, MESSAGE);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void sendSmsFailsWhenPhoneIsMissing(String phone) {
        SmsMessage sms = new SmsMessage(phone, MESSAGE);

        assertThatThrownBy(() -> smsUseCase.sendSms(sms))
                .isInstanceOf(RequiredFieldException.class)
                .hasMessage("El campo celular es obligatorio");
        verify(smsSenderPort, never()).send(anyString(), anyString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void sendSmsFailsWhenMessageIsMissing(String message) {
        SmsMessage sms = new SmsMessage("+573005698325", message);

        assertThatThrownBy(() -> smsUseCase.sendSms(sms))
                .isInstanceOf(RequiredFieldException.class)
                .hasMessage("El campo mensaje es obligatorio");
        verify(smsSenderPort, never()).send(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+5730056983251", "12345678901234", "300-569-8325", "abc", "57+300"})
    void sendSmsFailsWhenPhoneHasInvalidFormat(String phone) {
        SmsMessage sms = new SmsMessage(phone, MESSAGE);

        assertThatThrownBy(() -> smsUseCase.sendSms(sms)).isInstanceOf(InvalidPhoneException.class);
        verify(smsSenderPort, never()).send(anyString(), anyString());
    }
}
