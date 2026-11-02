package com.plazoleta.mensajeria.infrastructure.output.sms;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class SimulatedSmsSenderAdapterTest {

    @Test
    void sendOnlyLogsTheMessage() {
        SimulatedSmsSenderAdapter adapter = new SimulatedSmsSenderAdapter();

        assertThatCode(() -> adapter.send("+573005698325", "Hola")).doesNotThrowAnyException();
    }
}
