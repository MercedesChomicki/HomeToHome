# 🔧 Solución para el Problema de WebSocket CORS

## 🚨 Problema Identificado

El error de CORS que estabas experimentando se debía a una **configuración duplicada de CORS**:

1. **Gateway Server**: Aplicaba CORS a todas las rutas `/**`
2. **Chat Service**: Aplicaba CORS específicamente a WebSocket en `/ws` y `/info`

Cuando el gateway redirigía las peticiones al chat-service, ambos aplicaban headers de CORS, causando el error:
```
The 'Access-Control-Allow-Origin' header contains multiple values 'http://localhost:5173, http://localhost:5173', but only one is allowed.
```

## ✅ Solución Implementada

### 1. Gateway Server (`gateway-server`)

- **Nueva ruta WebSocket**: Agregada ruta específica `/api/ws/**` → `chat-service`
- **CORS específico**: CORS solo para rutas HTTP, no para WebSocket
- **Nueva clase**: `WebSocketConfig.java` para manejar CORS de WebSocket por separado

### 2. Chat Service (`chat-service`)

- **Endpoints duplicados**: Configurados tanto `/ws` como `/api/ws` para funcionar a través del gateway
- **CORS específico**: CORS solo para rutas de WebSocket
- **Mensajes privados**: Implementado sistema de mensajes privados usando `/user/queue/messages`
- **Autenticación JWT**: Configurada para usar la variable de entorno `JWT_SECRET` del docker-compose

### 3. Frontend (`src/`)

- **Identificación por email**: Uso del email como identificador único en lugar de IDs genéricos
- **Suscripción correcta**: Suscripción a `/user/{email}/queue/messages`
- **Manejo de estado**: Mejorado el manejo del estado de conexión

## 🔐 Configuración JWT

El chat-service ahora está configurado para usar la variable de entorno `JWT_SECRET` que está definida en tu archivo `.env.dev`:

```yaml
# En docker-compose.yml
chat-service:
  environment:
    - JWT_SECRET=${JWT_SECRET}

# En application.yml del chat-service
jwt:
  secret: ${JWT_SECRET}
```

## 🚀 Pasos para Aplicar la Solución

### 1. Reiniciar los Servicios

```bash
# Detener todos los servicios
docker-compose down

# Reconstruir y reiniciar
docker-compose up --build
```

### 2. Verificar la Configuración

- **Gateway**: Debe estar corriendo en puerto 8080
- **Chat Service**: Debe estar corriendo y registrado en Eureka
- **Frontend**: Debe estar corriendo en puerto 5173

### 3. Probar la Conexión

1. Abrir dos ventanas del navegador
2. Hacer login con diferentes usuarios:
   - `a@email.com` / `password`
   - `b@email.com` / `password`
3. Verificar que el WebSocket se conecte correctamente
4. Enviar mensajes entre usuarios

## 🔍 Archivos Modificados

### Backend
- `gateway-server/src/main/resources/application.yml`
- `gateway-server/src/main/java/com/hometohome/gateway_server/GatewayConfig.java`
- `gateway-server/src/main/java/com/hometohome/gateway_server/WebSocketConfig.java` (nuevo)
- `chat-service/src/main/java/com/hometohome/chat_service/config/WebSocketConfig.java`
- `chat-service/src/main/java/com/hometohome/chat_service/config/SecurityConfig.java`
- `chat-service/src/main/java/com/hometohome/chat_service/controller/ChatController.java`
- `chat-service/src/main/resources/application.yml`

### Frontend
- `src/services/chatService.js`
- `src/components/ChatComponent.jsx`
- `src/App.jsx`

## 🎯 Resultado Esperado

- ✅ WebSocket se conecta sin errores de CORS
- ✅ Mensajes se envían y reciben correctamente
- ✅ Conexión estable y reconexión automática
- ✅ Identificación única por email de usuario
- ✅ Autenticación JWT funcionando correctamente

## 🐛 Si Persisten los Problemas

1. **Verificar logs**: Revisar logs del gateway y chat-service
2. **Puertos**: Confirmar que no hay conflictos de puertos
3. **Dependencias**: Verificar que todas las dependencias estén corriendo
4. **Caché del navegador**: Limpiar caché y cookies
5. **Variable JWT_SECRET**: Confirmar que esté definida en `.env.dev`

## 📚 Recursos Adicionales

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Protocol](https://stomp.github.io/)
