package com.plazoleta.mensajeria.infrastructure.output.sms;

import com.plazoleta.mensajeria.domain.spi.ISmsSenderPort;
import lombok.extern.slf4j.Slf4j;

/**
 * Proveedor por defecto (sms.provider=simulado): no envia SMS reales, los registra en el log.
 */
@Slf4j
public class SimulatedSmsSenderAdapter implements ISmsSenderPort {

    @Override
    public void send(String phoneNumber, String message) {
        log.info("[SMS SIMULADO] Para: {} | Mensaje: {}", phoneNumber, message);
    }
}
