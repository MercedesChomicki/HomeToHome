# 🧩 H2H: Hometohome

Este proyecto implementa una arquitectura de microservicios utilizando **Java + Spring Boot**. Está diseñado para ser modular, escalable y fácil de mantener.

## 📦 Estructura del Proyecto

```bash
/mi-plataforma
├── user-service          # Microservicio para gestión de usuarios
├── pet-service           # Microservicio para gestión de mascotas (implementado)
│   ├── Dockerfile        # Imagen Docker para pet-service
│   ├── docker-compose.yml 
├── chat-service          # Microservicio para gestión de chats (en progreso)
└── ...
```
## 🚀 Microservicios 
### ✅ user-service
#### Servicio encargado de:
- Registrar usuarios 
- Buscar usuarios 
- Autenticación (futura implementación)

### ⏳ pet-service 
#### Servicio encargado de:
- Registrar mascotas
- Consultar mascotas por ID
- Listar todas las mascotas
- Actualizar mascotas
- Eliminar mascotas
- Obtener el dueño de una mascota por ID (nuevo endpoint)

#### Tecnologías de user y pet-service:
- Java 21+
- Spring Boot
- Spring Web
- Spring Data JPA
- MapStruct
- Feign Client
- PostgreSQL
- Docker

### chat-service
Actualmente está definido en la estructura del proyecto, pero todavía no implementado.

## 🧱 Arquitectura
Cada microservicio sigue una arquitectura en capas:
- **Controller:** expone endpoints REST
- **Service:** lógica de negocio
- **Repository:** acceso a datos (JPA)
- **Model:** entidades del dominio
- **DTOs y Mappers:** para mapear entre entidades y respuestas de API (usando MapStruct)

## 🐳 Docker
Cada microservicio puede contener su propio Dockerfile. Además, se puede utilizar un archivo docker-compose.yml a nivel raíz para orquestar contenedores (actualmente solo en pet-service).

## 🛠️ Cómo correr el proyecto
#### Prerrequisitos
- Java 21+
- Maven 
- Docker (opcional)

#### Ejecutar un user-service
```bash
cd user-service
./mvnw spring-boot:run
```

#### Ejecutar pet-service con Docker
```bash
cd pet-service
docker-compose up --build # si hubo cambios
docker-compose up # si no hubo cambios
```

## 🧪 Tests
Cada microservicio tendrá sus propias pruebas unitarias e integradas. Por el momento, no se incluyen tests.

## 📈 Estado del desarrollo
| Servicio | Estado        |
|----------|---------------|
| User     | ✅ Implementado (estructura base) |
| Pet      | ✅ Implementado + endpoint Feign |
| Chat     | 🚧 En desarrollo |

## 📄 Archivo .env
Creá un archivo **.env** en la raíz del proyecto con las variables necesarias. Por ejemplo:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pet-service-db
SPRING_DATASOURCE_USERNAME=<tu_usuario>
SPRING_DATASOURCE_PASSWORD=<tu_contraseña>
USER_SERVICE_URL=http://localhost:8081
```
Reemplazá los valores entre <> por los correspondientes a tu entorno.

⚠️ No compartas ni publiques este archivo. Asegurate de que .env esté en el archivo .gitignore.

## ✍️ Autora
María Mercedes Chomicki