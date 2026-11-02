package com.plazoleta.mensajeria.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SMS a enviar")
public class SmsRequestDto {

    @Schema(example = "+573005698325", description = "Celular del destinatario: maximo 13 caracteres, + opcional")
    @NotBlank(message = "El celular es obligatorio")
    @Pattern(regexp = "^(\\+\\d{1,12}|\\d{1,13})$",
            message = "El celular debe tener maximo 13 caracteres, solo digitos y opcionalmente el simbolo + al inicio")
    private String phoneNumber;

    @Schema(example = "Tu pedido #7 esta listo. Presenta este PIN para reclamarlo: 482913")
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "El mensaje no puede superar 500 caracteres")
    private String message;
}
