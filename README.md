# 🧩 H2H: Hometohome

Este proyecto implementa una arquitectura de microservicios utilizando **Java + Spring Boot**. Está diseñado para ser modular, escalable y fácil de mantener.

## 📦 Estructura del Proyecto

```bash
/H2H
├── config-server         # Servidor de configuración centralizada
│   ├── Dockerfile        # Imagen Docker para config-server
├── eureka-server         # Servidor de descubrimiento de servicios (Eureka)
│   ├── Dockerfile        # Imagen Docker para eureka-server
├── gateway-server        # Gateway API para enrutar solicitudes a los microservicios
│   ├── Dockerfile        # Imagen Docker para gateway-server
├── auth-service          # Microservicio de autenticación centralizada (nuevo)
│   ├── Dockerfile        # Imagen Docker para auth-service
├── user-service          # Microservicio para gestión de usuarios
│   ├── Dockerfile        # Imagen Docker para user-service
├── pet-service           # Microservicio para gestión de mascotas
│   ├── Dockerfile        # Imagen Docker para pet-service
├── chat-service          # Microservicio para gestión de chats (en progreso)
│   ├── Dockerfile        # Imagen Docker para chat-service
├── .env.dev              # Variables de entorno para desarrollo
├── .env.prod             # Variables de entorno para producción
├── docker-compose.yml    # Orquestación de todos los servicios
└── README.md
```
## 🚀 Microservicios 
### ✅ auth-service
Responsabilidades:
- Generar y validar tokens JWT (usuarios y microservicios internos).  
- Exponer endpoint público con clave pública vía `.well-known/jwks.json`.  
- Registrar e iniciar sesión de usuarios (`/auth/register`, `/auth/login`).  
- Emitir tokens de servicio con rol `SERVICE` para llamadas internas entre microservicios.  

### ✅ user-service
Responsabilidades:
- Registro de usuarios.  
- Búsqueda y gestión de usuarios.  
- Validación de JWT generados por **auth-service**.  
- Protección de endpoints según rol (`USER`, `ADMIN`, `SERVICE`).

### ✅ pet-service 
Responsabilidades:
- Registrar mascotas.  
- Consultar mascotas por ID.  
- Listar todas las mascotas.  
- Actualizar mascotas.  
- Eliminar mascotas.  
- Obtener el dueño de una mascota por ID.

### ✅ config-server
Responsabilidades:
- Centralizar la configuración de todos los microservicios.  
- Cargar propiedades desde un repositorio remoto (`config-repo`).

### ✅ eureka-server
Responsabilidades:
- Registro y descubrimiento de microservicios.  

### ✅ gateway-server
Responsabilidades:
- Actuar como puerta de entrada única para todos los microservicios.  
- Enrutar solicitudes a los servicios correspondientes según sus rutas.  
- Aplicar filtros comunes como autenticación, logging o manejo de errores (futura mejora).  

### ✅ chat-service (en progreso)  
Microservicio responsable de la mensajería en tiempo real vía **WebSockets (STOMP)**.  

#### Avances implementados:
- **Autenticación en WebSocket**:  
  - Interceptor en `WebSocketConfig` que valida el token JWT enviado en el `CONNECT` del cliente.  
  - Se extrae el `userId` del `sub` del token y se asocia al usuario en el `SecurityContext` y en un `StompPrincipal`.  
  - Se implementó `PrincipalContextHolder` para propagar el principal en hilos secundarios y llamadas Feign.  

- **Mensajería privada**:  
  - Endpoint `@MessageMapping("/chat")` en `ChatController` que permite enviar mensajes a un usuario específico.  
  - Los mensajes se envían a través de `/user/{id}/queue/messages`.  

- **Comunicación con user-service**:  
  - Cliente Feign `UserServiceClient` para obtener información de usuarios.  
  - Circuit Breaker + Retry con **Resilience4j** para tolerancia a fallos en la comunicación.  

- **Seguridad y Feign**:  
  - Interceptor `FeignAuthInterceptor` que reenvía el token JWT del usuario en llamadas Feign, o usa un **service token** de fallback para llamadas internas.  
  - Configuración de `SecurityContextPropagationConfig` para heredar el `SecurityContext` en hilos hijos.  

- **Manejo de errores**:  
  - `GlobalExceptionHandler` centralizado para excepciones comunes en REST y WebSockets.  

#### Pendiente de refactor:  
- **Integración con auth-service** para validar tokens JWT con la clave pública publicada en `/.well-known/jwks.json`, en lugar de usar `jwt.secret` local.  
- Persistencia de mensajes (actualmente en memoria).  
- Endpoints REST para historial de chat. 

## 🔑 Autenticación y Seguridad
La autenticación está **centralizada en `auth-service`**, que genera y valida tokens JWT firmados con RSA.  
Los microservicios consumidores validan la firma del token usando el JWKS público expuesto en:
```
GET http://auth-service/.well-known/jwks.json
```

### Roles definidos
- **USER** → usuarios finales logueados.  
- **ADMIN** → administradores con permisos avanzados.  
- **SERVICE** → tokens usados para comunicación interna entre microservicios. 


### Roles definidos
- **USER** → usuarios finales logueados.  
- **ADMIN** → administradores con permisos avanzados.  
- **SERVICE** → tokens usados para comunicación interna entre microservicios.  

### Flujo de autenticación
1. El usuario se registra o inicia sesión en `auth-service` (`/auth/register` o `/auth/login`).  
   - Respuesta: un JWT firmado con rol `USER` o `ADMIN`.  
2. El usuario utiliza ese JWT para consumir endpoints protegidos en `user-service`, `pet-service`, etc.  
3. Para llamadas internas entre microservicios (ejemplo: `auth-service` → `user-service`), se utiliza un **token de servicio** con rol `SERVICE` y un `audience` correspondiente.  
4. Los microservicios validan el JWT automáticamente contra el JWKS expuesto por `auth-service`.  

### Seguridad implementada
- 🔒 **Spring Security + Resource Server** para validar tokens.  
- 🔑 **Nimbus JOSE + JWKS** para firma/verificación de JWT con RSA.  
- 🛡️ Restricciones de acceso basadas en **roles y audiencias**.  

---

#### ⚙️ Tecnologías utilizadas
-  🟨 **Java 21+:** Lenguaje de programación moderno, robusto y orientado a objetos.

-  🟩 **Spring Boot:** Framework que simplifica la creación de microservicios en Java. Este proyecto utiliza:
   - **Spring Web:** Para exponer APIs REST.
   - **Spring Data JPA:** Para persistencia de datos. 
   - **Spring Cloud Config:** Para configuración centralizada de los servicios. 
   - **Spring Cloud Eureka Client:** Para el registro y descubrimiento de microservicios.
   - **Spring Cloud Gateway:** Para enrutar solicitudes a los microservicios mediante el gateway-server. 
   - **Spring Security + Resource Server:** Para proteger microservicios usando JWT emitidos por auth-service.

- 🛠️ **MapStruct:** Generador de código para convertir automáticamente entre entidades y DTOs, mejorando la mantenibilidad.

- 🌐 **Feign Client:** Cliente HTTP declarativo que facilita la comunicación entre microservicios.

- 🗄️ **PostgreSQL:** Base de datos relacional utilizada para almacenar la información de cada servicio.

- 🐳 **Docker:** Plataforma de contenedores para empaquetar los microservicios con sus dependencias.

- 📦 **Docker Compose:** Herramienta para orquestar múltiples contenedores desde un único archivo de configuración.

- 🔄 **Resilience4j:** Tolerancia a fallos con patrones como Circuit Breaker y Retry.

---

## 🧱 Arquitectura
Cada microservicio sigue una arquitectura en capas:
- **Controller** → expone endpoints REST.  
- **Service** → lógica de negocio.  
- **Repository** → acceso a datos (JPA).  
- **Model** → entidades de dominio.  
- **DTOs + Mappers** → conversión con MapStruct.

---

## 🐳 Docker & Entornos
El archivo `docker-compose.yml` en la raíz permite levantar todos los microservicios y dependencias (DB, Eureka, Config Server, etc.).

### Entornos separados
- **.env.dev** → entorno de desarrollo.  
- **.env.prod** → entorno de producción.  

---

## 🛠️ Cómo correr el proyecto
### Requisitos
- Java 21+
- Maven 
- Docker + Docker Compose

### Ejecución con Docker Compose
Usá el archivo correspondiente según el entorno deseado:
```bash
# Desarrollo
docker-compose --env-file .env.dev up --build

# Producción
docker-compose --env-file .env.prod up --build
```

## 🧪 Tests
Cada microservicio tendrá sus propias pruebas unitarias e integradas. Por el momento, no se incluyen tests.

## 🔧 Configuración de desarrollo
Se incluye un archivo .gitattributes para normalizar los finales de línea entre sistemas operativos y evitar conflictos de formato.

## 📈 Estado del desarrollo
| Servicio       | Estado                           |
|----------------|----------------------------------|
| User           | ✅ Implementado (autenticación vía auth-service)|
| Pet            | ✅ Implementado     |
| Config Server  | ✅ Implementado                   |
| Eureka Server  | ✅ Implementado                   |
| Gateway Server | ✅ Implementado                   |
| Auth Service   | ✅ Implementado (JWT + JWKS)               |
| Chat           | 🚧 En progreso (WebSocket + seguridad)                |

## 📄 Archivo .env
Ejemplo de `.env.dev`:

```bash
PET_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pet-service-db
PET_DATASOURCE_USERNAME=<tu_usuario>
PET_DATASOURCE_PASSWORD=<tu_contraseña>
USER_SERVICE_URL=http://localhost:8081
```

---

## 🚀 Últimos avances
- Creación de `auth-service` como punto central de autenticación.  
- Generación de JWT firmados con claves RSA.  
- Exposición de JWKS (`/.well-known/jwks.json`) para validación distribuida.  
- Separación clara entre **usuarios finales** y **tokens de servicio**.  
- Integración de seguridad en `user-service` con validación de roles y audiencias.  
- Configuración de Feign con `RequestInterceptor` para comunicación segura entre microservicios.  

---

## ✍️ Autora
María Mercedes Chomicki