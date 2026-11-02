package com.plazoleta.mensajeria.infrastructure.configuration;

import com.plazoleta.mensajeria.domain.api.ISmsServicePort;
import com.plazoleta.mensajeria.domain.spi.ISmsSenderPort;
import com.plazoleta.mensajeria.domain.usecase.SmsUseCase;
import com.plazoleta.mensajeria.infrastructure.output.sms.SimulatedSmsSenderAdapter;
import com.plazoleta.mensajeria.infrastructure.output.sms.TwilioSdkClient;
import com.plazoleta.mensajeria.infrastructure.output.sms.TwilioSmsSenderAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    /**
     * Proveedor por defecto: SMS simulados (se registran en el log).
     */
    @Bean
    @ConditionalOnProperty(name = "sms.provider", havingValue = "simulado", matchIfMissing = true)
    public ISmsSenderPort simulatedSmsSenderPort() {
        return new SimulatedSmsSenderAdapter();
    }

    /**
     * Proveedor real opcional: se activa con SMS_PROVIDER=twilio y las variables TWILIO_*.
     */
    @Bean
    @ConditionalOnProperty(name = "sms.provider", havingValue = "twilio")
    public ISmsSenderPort twilioSmsSenderPort(@Value("${sms.twilio.account-sid}") String accountSid,
                                              @Value("${sms.twilio.api-key-sid}") String apiKeySid,
                                              @Value("${sms.twilio.api-key-secret}") String apiKeySecret,
                                              @Value("${sms.twilio.from-number}") String fromNumber,
                                              @Value("${sms.default-country-code}") String defaultCountryCode) {
        return new TwilioSmsSenderAdapter(new TwilioSdkClient(accountSid, apiKeySid, apiKeySecret),
                fromNumber, defaultCountryCode);
    }

    @Bean
    public ISmsServicePort smsServicePort(ISmsSenderPort smsSenderPort) {
        return new SmsUseCase(smsSenderPort);
    }
}
