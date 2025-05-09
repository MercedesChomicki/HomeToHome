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
├── user-service          # Microservicio para gestión de usuarios
│   ├── Dockerfile        # Imagen Docker para user-service
├── pet-service           # Microservicio para gestión de mascotas
│   ├── Dockerfile        # Imagen Docker para pet-service
├── chat-service          # Microservicio para gestión de chats (en progreso)
├── .env.dev              # Variables de entorno para desarrollo
├── .env.prod             # Variables de entorno para producción
├── docker-compose.yml    # Orquestación de todos los servicios
└── README.md
```
## 🚀 Microservicios 
### ✅ user-service
#### Responsabilidades:
- Registrar usuarios 
- Buscar usuarios 
- Autenticación (futura implementación)

### ✅ pet-service 
#### Responsabilidades:
- Registrar mascotas
- Consultar mascotas por ID
- Listar todas las mascotas
- Actualizar mascotas
- Eliminar mascotas
- Obtener el dueño de una mascota por ID (nuevo endpoint)

### ✅ config-server
#### Responsabilidades:
- Centralizar la configuración de todos los microservicios
- Cargar propiedades desde un repositorio remoto (config-repo)

### ✅ eureka-server
#### Responsabilidades:
- Registro y descubrimiento de microservicios

### ✅ gateway-server
#### Responsabilidades:
- Actuar como puerta de entrada única para todos los microservicios
- Enrutar las solicitudes a los servicios correspondientes según sus rutas
- Aplicar filtros comunes como autenticación, logging o manejo de errores (futura mejora)

### ⏳ chat-service
Actualmente está definido en la estructura del proyecto, pero todavía no implementado.

#### ⚙️ Tecnologías utilizadas
-  🟨 **Java 21+:** Lenguaje de programación moderno, robusto y orientado a objetos.


-  🟩 **Spring Boot:** Framework que simplifica la creación de microservicios en Java. Este proyecto utiliza:
   - **Spring Web:** Para exponer APIs REST.
   - **Spring Data JPA:** Para persistencia de datos. 
   - **Spring Cloud Config:** Para configuración centralizada de los servicios. 
   - **Spring Cloud Eureka Client:** Para el registro y descubrimiento de microservicios.


- 🛠️ **MapStruct:** Generador de código para convertir automáticamente entre entidades y DTOs, mejorando la mantenibilidad.


- 🌐 **Feign Client:** Cliente HTTP declarativo que facilita la comunicación entre microservicios.


- 🗄️ **PostgreSQL:** Base de datos relacional utilizada para almacenar la información de cada servicio.

 
- 🐳 **Docker:** Plataforma de contenedores para empaquetar los microservicios con sus dependencias.


- 📦 **Docker Compose:** Herramienta para orquestar múltiples contenedores desde un único archivo de configuración.

## 🧱 Arquitectura
Cada microservicio sigue una arquitectura en capas:
- **Controller:** expone endpoints REST
- **Service:** lógica de negocio
- **Repository:** acceso a datos (JPA)
- **Model:** entidades del dominio
- **DTOs y Mappers:** para mapear entre entidades y respuestas de API (usando MapStruct)

## 🐳 Docker & Entornos
El archivo docker-compose.yml en la raíz permite levantar todos los microservicios y sus dependencias (base de datos, Eureka, Config Server, etc.).
### Entornos separados
- **.env.dev:** variables para entorno de desarrollo
- **.env.prod:** variables para entorno de producción

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
| User           | ✅ Implementado (estructura base) |
| Pet            | ✅ Implementado + Feign client    |
| Config Server  | ✅ Implementado                   |
| Eureka Server  | ✅ Implementado                   |
| Gateway Server | ✅ Implementado                   |
| Chat           | 🚧 En desarrollo                 |

## 📄 Archivo .env
Creá un archivo **.env.dev** en la raíz del proyecto con las variables necesarias. Por ejemplo:

```bash
PET_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pet-service-db
PET_DATASOURCE_USERNAME=<tu_usuario>
PET_DATASOURCE_PASSWORD=<tu_contraseña>
USER_SERVICE_URL=http://localhost:8081
```
⚠️ Importante: No compartas este archivo. Asegurate de que .env.dev y .env.prod estén en el archivo .gitignore.

## ✍️ Autora
María Mercedes Chomicki