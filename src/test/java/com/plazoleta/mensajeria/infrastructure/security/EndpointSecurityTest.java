package com.plazoleta.mensajeria.infrastructure.security;

import com.plazoleta.mensajeria.application.dto.response.SmsResponseDto;
import com.plazoleta.mensajeria.application.handler.ISmsHandler;
import com.plazoleta.mensajeria.domain.exception.InvalidPhoneException;
import com.plazoleta.mensajeria.infrastructure.exceptionhandler.ControllerAdvisor;
import com.plazoleta.mensajeria.infrastructure.input.rest.SmsRestController;
import com.plazoleta.mensajeria.infrastructure.output.sms.exception.SmsDeliveryException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU-14: POST /sms exige token valido con rol EMPLEADO y responde los errores en JSON.
 */
@WebMvcTest(controllers = SmsRestController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class, ControllerAdvisor.class})
@TestPropertySource(properties = "security.jwt.secret=" + EndpointSecurityTest.SECRET)
class EndpointSecurityTest {

    static final String SECRET = "cGxhem9sZXRhLWRlLWNvbWlkYXMtc2VjcmV0by1zdXBlci1zZWd1cm8tMjU2LWJpdHM=";
    private static final String BODY = "{\"phoneNumber\":\"+573005698325\",\"message\":\"Tu pedido esta listo\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ISmsHandler smsHandler;

    /** Token firmado con la misma clave que usa plazoleta-usuarios. */
    private String bearer(String role) {
        Date now = new Date();
        String token = Jwts.builder()
                .subject("empleado@plazoleta.com")
                .claim("id", 5L)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 60_000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();
        return "Bearer " + token;
    }

    @Test
    void sendSmsAsEmployeeReturns200() throws Exception {
        when(smsHandler.sendSms(any())).thenReturn(new SmsResponseDto("ENVIADO", "+573005698325"));

        mockMvc.perform(post("/sms").header(HttpHeaders.AUTHORIZATION, bearer("EMPLOYEE"))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENVIADO"))
                .andExpect(jsonPath("$.phoneNumber").value("+573005698325"));
    }

    @Test
    void sendSmsWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/sms").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void sendSmsWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(post("/sms").header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "OWNER", "CLIENT"})
    void sendSmsWithOtherRoleReturns403(String role) throws Exception {
        mockMvc.perform(post("/sms").header(HttpHeaders.AUTHORIZATION, bearer(role))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void sendSmsWithInvalidBodyReturns400WithDetails() throws Exception {
        mockMvc.perform(post("/sms").header(HttpHeaders.AUTHORIZATION, bearer("EMPLOYEE"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"phoneNumber\":\"abc\",\"message\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.length()").value(2));
    }

    @Test
    void sendSmsWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/sms").header(HttpHeaders.AUTHORIZATION, bearer("EMPLOYEE"))
                        .contentType(MediaType.APPLICATION_JSON).content("{malformado"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendSmsWhenDomainRejectsReturns400() throws Exception {
        when(smsHandler.sendSms(any())).thenThrow(new InvalidPhoneException());

        mockMvc.perform(post("/sms").header(HttpHeaders.AUTHORIZATION, bearer("EMPLOYEE"))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendSmsWhenProviderFailsReturns503() throws Exception {
        when(smsHandler.sendSms(any())).thenThrow(new SmsDeliveryException(new IllegalStateException("caido")));

        mockMvc.perform(post("/sms").header(HttpHeaders.AUTHORIZATION, bearer("EMPLOYEE"))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }
}
