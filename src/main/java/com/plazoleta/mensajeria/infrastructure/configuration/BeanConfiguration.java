package com.plazoleta.mensajeria.infrastructure.configuration;

import com.plazoleta.mensajeria.domain.api.ISmsServicePort;
import com.plazoleta.mensajeria.domain.spi.ISmsSenderPort;
import com.plazoleta.mensajeria.domain.usecase.SmsUseCase;
import com.plazoleta.mensajeria.infrastructure.output.sms.SimulatedSmsSenderAdapter;
import com.plazoleta.mensajeria.infrastructure.output.sms.TwilioSdkClient;
import com.plazoleta.mensajeria.infrastructure.output.sms.TwilioSmsSenderAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BeanConfiguration {

    private static final String PROVIDER_SIMULATED = "simulado";
    private static final String PROVIDER_TWILIO = "twilio";

    /**
     * Unico proveedor de SMS, segun sms.provider (variable SMS_PROVIDER):
     * "simulado" (por defecto) registra los SMS en el log; "twilio" los envia de verdad con las variables TWILIO_*.
     */
    @Bean
    public ISmsSenderPort smsSenderPort(@Value("${sms.provider:" + PROVIDER_SIMULATED + "}") String provider,
                                        Environment environment) {
        return switch (provider.trim().toLowerCase()) {
            case PROVIDER_SIMULATED -> new SimulatedSmsSenderAdapter();
            case PROVIDER_TWILIO -> new TwilioSmsSenderAdapter(
                    new TwilioSdkClient(environment.getProperty("sms.twilio.account-sid"),
                            environment.getProperty("sms.twilio.api-key-sid"),
                            environment.getProperty("sms.twilio.api-key-secret")),
                    environment.getProperty("sms.twilio.from-number"),
                    environment.getProperty("sms.default-country-code"));
            default -> throw new IllegalStateException(
                    "sms.provider invalido: '" + provider + "'. Valores permitidos: simulado o twilio");
        };
    }

    @Bean
    public ISmsServicePort smsServicePort(ISmsSenderPort smsSenderPort) {
        return new SmsUseCase(smsSenderPort);
    }
}
