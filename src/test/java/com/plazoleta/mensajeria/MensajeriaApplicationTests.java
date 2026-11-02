package com.plazoleta.mensajeria;

import com.plazoleta.mensajeria.domain.spi.ISmsSenderPort;
import com.plazoleta.mensajeria.infrastructure.output.sms.SimulatedSmsSenderAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MensajeriaApplicationTests {

    @Autowired
    private ISmsSenderPort smsSenderPort;

    @Test
    void contextLoadsWithSimulatedProviderByDefault() {
        assertThat(smsSenderPort).isInstanceOf(SimulatedSmsSenderAdapter.class);
    }
}
