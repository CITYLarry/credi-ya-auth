# Copilot Instructions - CrediYa Authentication Service

## Architecture Overview

This is a **Hexagonal Architecture** (Ports & Adapters) Spring WebFlux microservice with **strict multi-module separation**:

- `domain/model`: Pure domain entities with built-in validation (e.g., `User.java`)
- `domain/port/out`: Outbound ports for external dependencies (e.g., `UserRepository`)
- `applications/port/in`: Inbound ports defining use cases (e.g., `RegisterUserPort`)
- `applications/service`: Use case implementations (e.g., `RegisterUserUseCase`)
- `applications/exception`: Business exceptions (e.g., `EmailAlreadyExistsException`)
- `infrastructure/adapter/drivin/web`: REST controllers and DTOs
- `infrastructure/adapter/driven/persistence`: Database adapters with R2DBC
- `applications/app-auth`: Executable Spring Boot application module

## Key Patterns

### Domain-Driven Design
- **Domain models are immutable** - use factory methods like `User.newUser()` for creation
- **Validation in domain**: Email patterns, salary ranges enforced in `User` constructor
- **Rich domain objects**: Business logic stays in domain, not services

### Reactive Programming
- **Always use `Mono<T>`/`Flux<T>`** for async operations
- **StepVerifier** for testing reactive streams: `StepVerifier.create(mono).expectNext().verifyComplete()`
- **R2DBC** for reactive database access, not JPA

### Testing Strategy
- **Unit tests**: Mock dependencies with `@ExtendWith(MockitoExtension.class)`
- **Integration tests**: Use `@DataR2dbcTest` for persistence layer
- **Web tests**: Use `@WebFluxTest` with `WebTestClient`
- **Test naming**: `shouldReturnExpectedWhenGivenCondition()`

### Module Dependencies
```
applications/service -> applications/port/in + domain/port/out + domain/model
infrastructure/adapter/driven/persistence -> domain/port/out + domain/model
infrastructure/adapter/drivin/web -> applications/port/in + domain/model
```

## Development Workflows

### Building & Testing
```bash
./gradlew clean build                    # Full build all modules
./gradlew :applications:service:test     # Test specific module
./gradlew test --info                   # Run with detailed output
./gradlew bootRun                       # Start application (from root)
```

### Coverage in IntelliJ
- Set **Settings → Build Tools → Gradle → Run tests using** to "IntelliJ IDEA"
- Use **Run All Tests with Coverage** for native IntelliJ coverage

### Database Access
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:credityadb`
- Schema auto-created from `schema.sql`

## Project-Specific Conventions

### Error Handling
- Business exceptions in `applications/exception` module
- Controllers handle exceptions with `@ExceptionHandler`
- Always log with trace level: `log.trace("Attempting to register user with email: {}", email)`

### Command Pattern
- Use record-based commands: `RegisterUserCommand(firstName, lastName, email, ...)`
- Commands have `toDomainUser()` method to create domain objects

### Logging
- **Trace level** for business operations
- **Warn level** for business rule violations
- Include relevant business data in log messages

### API Design
- REST endpoints in `/api/v1/` namespace
- OpenAPI documentation with `@Operation`, `@ApiResponse`
- Request/Response DTOs in `infrastructure.entrypoints.web.dto`
- Validation with `@Valid` on controller methods

### Gradle Configuration
- Each module has independent `build.gradle`
- Main app in `applications/app-auth` with executable JAR
- Subprojects share common dependencies via root `build.gradle`

## Integration Points

### Technology Stack
- **Spring Boot 3.2.5** with **Java 17**
- **Spring WebFlux** for reactive web
- **Spring Data R2DBC** with H2 database
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction

### External Dependencies
- Database: H2 in-memory for development
- Future: PostgreSQL with R2DBC for production
- API Documentation: OpenAPI/Swagger UI

## Database schemas

Relational Database Schema:

---

Table: Usuario
- id_usuario (PK): INT, Unique ID for the user.
- nombre: VARCHAR, User's first name.
- apellido: VARCHAR, User's last name.
- email: VARCHAR, User's email.
- documento_identidad: VARCHAR, User's identification number.
- telefono: VARCHAR, User's phone number.
- id_rol (FK): INT, References Rol table.
- salario_base: DECIMAL, User's base salary.

---

Table: Rol
- UniqueId (PK): INT, Unique ID for the role.
- nombre: VARCHAR, Name of the role (e.g., 'admin', 'client').
- descripcion: VARCHAR, Description of the role.

---

Table: solicitud
- id_solicitud (PK): INT, Unique ID for the request.
- monto: DECIMAL, Requested amount.
- plazo: INT, Loan term.
- email: VARCHAR, Applicant's email.
- id_estado (FK): INT, References estados table.
- id_tipo_prestamo (FK): INT, References tipo_prestamo table.

---

Table: estados
- id_estado (PK): INT, Unique ID for the status.
- nombre: VARCHAR, Status name (e.g., 'approved', 'pending').
- descripcion: VARCHAR, Status description.

---

Table: tipo_prestamo
- id_tipo_prestamo (PK): INT, Unique ID for the loan type.
- nombre: VARCHAR, Loan type name (e.g., 'mortgage', 'personal').
- monto_minimo: DECIMAL, Minimum amount for this loan type.
- monto_maximo: DECIMAL, Maximum amount for this loan type.
- tasa_interes: DECIMAL, Interest rate.
- validacion_automatica: BOOLEAN, Flag for automatic validation.
