package com.plazoleta.mensajeria.infrastructure.input.rest;

import com.plazoleta.mensajeria.application.dto.request.SmsRequestDto;
import com.plazoleta.mensajeria.application.dto.response.SmsResponseDto;
import com.plazoleta.mensajeria.application.handler.ISmsHandler;
import com.plazoleta.mensajeria.infrastructure.exceptionhandler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Tag(name = "SMS", description = "Envio de SMS a los clientes")
public class SmsRestController {

    private final ISmsHandler smsHandler;

    @Operation(summary = "Enviar SMS",
            description = "Envia un SMS al celular indicado (lo usa plazoleta para enviar el PIN de un pedido listo). "
                    + "Por defecto el envio es simulado y queda registrado en el log. Solo disponible para el rol "
                    + "EMPLEADO.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS enviado",
                    content = @Content(schema = @Schema(implementation = SmsResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Celular o mensaje faltantes o con formato invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es EMPLEADO",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "El proveedor de SMS no esta disponible",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SmsResponseDto> sendSms(@Valid @RequestBody SmsRequestDto smsRequestDto) {
        return ResponseEntity.ok(smsHandler.sendSms(smsRequestDto));
    }
}
