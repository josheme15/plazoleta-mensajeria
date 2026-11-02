package com.plazoleta.mensajeria.infrastructure.output.sms;

import com.plazoleta.mensajeria.infrastructure.output.sms.exception.SmsDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TwilioSmsSenderAdapterTest {

    private static final String FROM = "+15005550006";

    @Mock
    private TwilioClient twilioClient;

    private TwilioSmsSenderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TwilioSmsSenderAdapter(twilioClient, FROM, "+57");
    }

    @Test
    void sendKeepsPhoneWhenItIsAlreadyInternational() {
        adapter.send("+573005698325", "Hola");

        verify(twilioClient).sendMessage("+573005698325", FROM, "Hola");
    }

    @Test
    void sendAddsDefaultCountryCodeWhenPhoneHasNoPlus() {
        adapter.send("3005698325", "Hola");

        verify(twilioClient).sendMessage("+573005698325", FROM, "Hola");
    }

    @Test
    void sendWrapsProviderErrors() {
        doThrow(new IllegalStateException("Twilio caido"))
                .when(twilioClient).sendMessage(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> adapter.send("+573005698325", "Hola"))
                .isInstanceOf(SmsDeliveryException.class)
                .hasMessage("No fue posible enviar el SMS: el proveedor de mensajeria no esta disponible");
    }
}
