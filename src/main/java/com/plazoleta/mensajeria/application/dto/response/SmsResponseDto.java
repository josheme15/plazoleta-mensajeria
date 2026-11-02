package com.plazoleta.mensajeria.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponseDto {

    @Schema(example = "ENVIADO")
    private String status;

    @Schema(example = "+573005698325")
    private String phoneNumber;
}
