# CrediYa - Authentication Service

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)](https://gradle.org/)

## Descripción

CrediYa Authentication Service es un microservicio de autenticación y gestión de usuarios desarrollado con Spring Boot y arquitectura hexagonal. Este servicio proporciona funcionalidades para el registro y manejo de usuarios en la plataforma CrediYa.

## Características

- 🏗️ **Arquitectura Hexagonal**: Implementación de Clean Architecture con separación clara de responsabilidades
- ⚡ **Reactive Programming**: Construido con Spring WebFlux para programación reactiva no bloqueante
- 🗄️ **Base de Datos en Memoria**: Utiliza H2 con R2DBC para desarrollo y pruebas
- 📚 **Documentación API**: Documentación automática con OpenAPI/Swagger
- ✅ **Validación**: Validación robusta de datos de entrada
- 🧪 **Testing**: Suite completa de pruebas unitarias e integración
- 🛠️ **MapStruct**: Mapeo automático entre DTOs y entidades de dominio
- 📊 **Lombok**: Reducción de código boilerplate

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring WebFlux** (Reactive Web)
- **Spring Data R2DBC** (Reactive Database Connectivity)
- **H2 Database** (In-memory database)
- **MapStruct** (Bean mapping)
- **Lombok** (Code generation)
- **OpenAPI 3** (API documentation)
- **JUnit 5** (Testing)
- **Gradle** (Build tool)

## Arquitectura

El proyecto sigue una arquitectura hexagonal (ports and adapters) con las siguientes capas:

```
src/main/java/com/crediya/auth/
├── application/          # Casos de uso y lógica de aplicación
│   ├── exceptions/       # Excepciones específicas de la aplicación
│   ├── ports/           # Puertos de entrada y salida
│   └── service/         # Implementación de casos de uso
├── domain/              # Lógica de negocio y entidades
│   ├── model/           # Entidades de dominio
│   └── ports/           # Puertos del dominio
└── infrastructure/      # Adaptadores e infraestructura
    └── adapters/
        ├── driven/      # Adaptadores de salida (base de datos, etc.)
        └── drivin/      # Adaptadores de entrada (web, etc.)
```

## Requisitos

- **Java 17** o superior
- **Gradle 8.x**
- **Git**

## Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/CITYLarry/crediya-auth.git
cd crediya-auth
```

### 2. Compilar el proyecto

```bash
./gradlew build
```

### 3. Ejecutar la aplicación

```bash
./gradlew bootRun
```

La aplicación estará disponible en: `http://localhost:8080`

## Endpoints de la API

### Registro de Usuario

```http
POST /api/v1/users
Content-Type: application/json
```

**Cuerpo de la petición:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@email.com",
  "identityNumber": "12345678",
  "phoneNumber": "+573001234567",
  "birthDate": "1990-05-15",
  "address": "Calle 123 #45-67",
  "idRole": "USER",
  "baseSalary": 3000000
}
```

**Respuesta exitosa (201):**
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@email.com",
  "message": "Usuario registrado exitosamente"
}
```

## Documentación de la API

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de la API:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Base de Datos

### Consola H2

La aplicación utiliza una base de datos H2 en memoria. Puedes acceder a la consola H2 para inspeccionar los datos:

- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:credityadb`
- **Usuario**: `sa`
- **Contraseña**: *(vacía)*

### Esquema de Base de Datos

El esquema se inicializa automáticamente desde `src/main/resources/schema.sql`.

## Testing

### Ejecutar todas las pruebas

```bash
./gradlew test
```

### Ejecutar pruebas con reporte

```bash
./gradlew test --info
```

Los reportes de pruebas se generan en: `build/reports/tests/test/index.html`

## Configuración

La configuración de la aplicación se encuentra en `src/main/resources/application.yaml`:

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///credityadb;DB_CLOSE_DELAY=-1
    username: sa
    password: ''
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always
```

## Validaciones de Dominio

El modelo `User` incluye las siguientes validaciones:

- **Email**: Formato válido de correo electrónico
- **Salario**: Entre 0 y 15,000,000
- **Campos obligatorios**: firstName, lastName, email, identityNumber, phoneNumber, birthDate, address, idRole

## Manejo de Errores

La aplicación maneja diferentes tipos de errores:

- **400 Bad Request**: Datos de entrada inválidos
- **409 Conflict**: Email ya existe en el sistema
- **500 Internal Server Error**: Errores internos del servidor

## Desarrollo

### Estructura del Código

- **Domain Models**: Entidades de negocio con validaciones incorporadas
- **Use Cases**: Lógica de aplicación encapsulada en casos de uso
- **Ports**: Interfaces que definen contratos entre capas
- **Adapters**: Implementaciones concretas de los puertos

### Comandos Útiles

```bash
# Limpiar y compilar
./gradlew clean build

# Ejecutar en modo desarrollo
./gradlew bootRun

# Generar JAR ejecutable
./gradlew bootJar

# Verificar dependencias
./gradlew dependencies
```

## Licencia

Este proyecto es parte de la plataforma CrediYa desarrollado por Pragma.

## Contacto

- **Desarrollador**: Larry Mateo Ramirez C.
- **Proyecto**: CrediYa Authentication Service
- **Fecha**: Agosto 2025

---

Para más información sobre el proyecto, consulta la documentación técnica o contacta al equipo de desarrollo.
