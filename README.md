# 🧩 H2H: Hometohome

Este proyecto implementa una arquitectura de microservicios utilizando **Java + Spring Boot**. Está diseñado para ser modular, escalable y fácil de mantener.

## 📦 Estructura del Proyecto

```bash
/mi-plataforma
├── user-service          # Microservicio para gestión de usuarios
├── pet-service           # Microservicio para gestión de mascotas (en progreso)
├── chat-service         # Microservicio para gestión de chats (en progreso)
└── ...
```
## 🚀 Microservicios 
### ✅ usuario-service
#### Servicio encargado de:
- Registrar usuarios 
- Buscar usuarios 
- Autenticación (futura implementación)

#### Tecnologías:
- Java 21+ 
- Spring Boot 
- Spring Web 
- Spring Data JPA 
- PostgreSQL

### ⏳ pet-service y chat-service
Actualmente están definidos en la estructura del proyecto, pero todavía no implementados.

## 🧱 Arquitectura
Cada microservicio sigue una arquitectura en capas:
- Controller: expone endpoints REST
- Service: lógica de negocio
- Repository: acceso a datos (JPA)
- Model: entidades del dominio

## 🛠️ Cómo correr el proyecto
#### Prerrequisitos
- Java 21+
- Maven 
- Docker (opcional)

#### Ejecutar un microservicio individual
```bash
cd user-service
./mvnw spring-boot:run
```

#### Compilar todos los servicios (si estás en un monorepo con Maven)
```bash
mvn clean install
```

## 📦 Docker (opcional)
Se puede agregar soporte para Docker con un Dockerfile por microservicio y un docker-compose.yml para orquestar los servicios.

## 🧪 Tests
Cada microservicio tendrá sus propias pruebas unitarias e integradas. Por el momento, no se incluyen tests.

## 📈 Estado del desarrollo
| Servicio | Estado        |
|----------|---------------|
| User     | ✅ Implementado (estructura base) |
| Pet      | 🚧 En desarrollo |
| Chat     | 🚧 En desarrollo |

## ✍️ Autora
María Mercedes Chomicki

