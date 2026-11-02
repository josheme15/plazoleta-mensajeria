# plazoleta-mensajeria

Microservicio de **mensajería** de la plazoleta de comidas (Spring Boot 3 + arquitectura hexagonal).
Responsable de enviar SMS a los clientes. No tiene base de datos.

## Historias de usuario implementadas

| HU | Descripción | Endpoint | Rol |
|----|-------------|----------|-----|
| HU-14 | Enviar el SMS con el PIN de un pedido listo | `POST /sms` | EMPLOYEE |

### Reglas de la HU-14

- `plazoleta-plazoleta` llama a `POST /sms` cuando el empleado marca un pedido como **LISTO**
  (`PATCH /orders/{id}/ready`), reenviando el token del empleado. El mensaje lleva el PIN de seguridad
  que el cliente presenta para reclamar su pedido.
- Campos obligatorios: `phoneNumber` y `message` (máximo 500 caracteres).
- Celular: máximo 13 caracteres, solo dígitos y `+` opcional al inicio (mismo formato que en usuarios).
- Solo rol `EMPLOYEE` (403 para los demás roles, 401 sin token o con token inválido).
- Si el proveedor de SMS falla, responde **503** y plazoleta deja el pedido EN_PREPARACION.

### Proveedor de SMS

El envío pasa por el puerto `ISmsSenderPort`, con dos adaptadores que se eligen con `SMS_PROVIDER`:

| `SMS_PROVIDER` | Adaptador | Qué hace |
|----------------|-----------|----------|
| `simulado` (por defecto) | `SimulatedSmsSenderAdapter` | No envía SMS reales: los registra en la consola (`[SMS SIMULADO] Para: ... \| Mensaje: ...`) |
| `twilio` | `TwilioSmsSenderAdapter` | Envía SMS reales con Twilio (agrega `SMS_DEFAULT_COUNTRY_CODE` si el celular no tiene `+`) |

Para usar Twilio las credenciales van **solo en variables de entorno**, nunca en el código:

```bash
SMS_PROVIDER=twilio
TWILIO_ACCOUNT_SID=ACxxxxxxxx
TWILIO_API_KEY_SID=SKxxxxxxxx
TWILIO_API_KEY_SECRET=********
TWILIO_FROM_NUMBER=+1xxxxxxxxxx
```

## Requisitos

- Java 21+
- Maven 3.9+
- `plazoleta-usuarios` (puerto 8081) para obtener el token del empleado

## Cómo levantarlo

```bash
mvn spring-boot:run           # Servicio en http://localhost:8083
```

Swagger UI: http://localhost:8083/swagger-ui.html
OpenAPI JSON: http://localhost:8083/v3/api-docs

### Variables de entorno

| Variable | Default |
|----------|---------|
| `SERVER_PORT` | `8083` |
| `JWT_SECRET` | misma clave que `plazoleta-usuarios` |
| `SMS_PROVIDER` | `simulado` (`twilio` para envío real) |
| `SMS_DEFAULT_COUNTRY_CODE` | `+57` |
| `TWILIO_ACCOUNT_SID` / `TWILIO_API_KEY_SID` / `TWILIO_API_KEY_SECRET` / `TWILIO_FROM_NUMBER` | vacíos |

## Uso rápido

```bash
# 1. Token de un EMPLEADO (microservicio de usuarios)
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" \
  -d '{"email":"<correo_empleado>","password":"<clave>"}'

# 2. Enviar el SMS
curl -X POST http://localhost:8083/sms \
  -H "Authorization: Bearer <TOKEN_EMPLEADO>" -H "Content-Type: application/json" \
  -d '{"phoneNumber":"+573005698325","message":"Tu pedido #7 esta listo. Presenta este PIN para reclamarlo: 482913"}'
```

## Pruebas con Postman

En la carpeta `postman/`:

- `plazoleta-mensajeria.postman_collection.json`: 19 peticiones y 23 aserciones.
- `plazoleta-local.postman_environment.json`: `baseUrl` (8083), `usuariosUrl` (8081), `plazoletaUrl` (8082)
  y credenciales del admin.

1. En Postman: **Import** → seleccionar ambos archivos.
2. Seleccionar el entorno **Plazoleta - Local (mensajeria)**.
3. Clic derecho sobre la colección → **Run collection** → **Run**.

`00 - Preparacion` obtiene un token de cada rol (crea propietario, restaurante, empleado y cliente) y
`01 - HU-14` envía SMS válidos y prueba los errores de validación, de rol y de token.
El flujo completo del pedido listo (plazoleta → usuarios → mensajería) está en la carpeta `12 - HU-14`
de la colección de `plazoleta-plazoleta`.

## Arquitectura

```
domain/            Java puro: modelo SmsMessage, puertos (api / spi), caso de uso, excepciones
application/       DTOs y handler
infrastructure/    Controller REST, adaptadores de SMS (simulado y Twilio), seguridad JWT,
                   manejo de errores y configuración
```

## Pruebas

```bash
mvn test                      # Reporte de cobertura en target/site/jacoco/index.html
```
