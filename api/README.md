# JobConnect Platform

**JobConnect** es una plataforma de intermediación de servicios locales (estilo Upwork/Uber para oficios). Conecta clientes con profesionales independientes (plomeros, electricistas, desarrolladores, etc.)

## Arquitectura y Tecnologías
- **Arquitectura**: Hexagonal (puertos y adaptadores) / Clean Architecture.
- **Lenguaje**: Java 25.
- **Framework**: Spring Boot 4.
- **Seguridad**: JWT con RSA (firma asimétrica) + Spring Security.
- **Persistencia**: Jpa / Hibernate, PostgreSQL, Flyway para migraciones.
- **Documentación**: Swagger / OpenAPI 3.
- **Build Tool**: Maven.

## Principios de diseños aplicados
- **DDD (Domain-Driven Design)**: Agregados (`Job`, `Offer`, `Transaction`), Objetos de Valor (`Money`, `Address`, `UserId`), Servicios de Dominio.
- **SOLID**: Especialmente la Inversión de Dependencias (DIP) mediante puertos y adaptadores.
- **Máquina de Estados**: Ciclo de vida de un `Job` (PUBLISHED → IN_PROGRESS → PENDING_CONFIRMATION → COMPLETED).

## Flujo de negocio principal
1. Un **Cliente** registra una cuenta y publica un `Job`.
2. Los **Profesionales** ven los trabajos publicados y envían una `Offer`.
3. El **Cliente** acepta una oferta (el `Job` pasa a `IN_PROGRESS` y se crea la `Transaction`).
4. El **Profesional** finaliza el trabajo (`mark-pending`, estado `PENDING_CONFIRMATION`).
5. El **Cliente** confirma la finalización (`confirm-completion`, estado `COMPLETED` y se libera el pago).

## Cómo ejecutar el proyecto
1. **Clonar el repositorio**
```bash
git clone https://github.com/lopez-justin/jobconnect-platform.git
```

2. **Levantar los servicios con Docker**
```bash
docker-compose up -d
```
3. **Configurar variables de entorno**
- Establecer las variables de entorno necesarias para la conexión a la base de datos y otros servicios.
- ADMIN_EMAIL=admin.app@email.com;ADMIN_PASSWORD=adminapp123;DB_HOST=localhost;DB_NAME=jobconnect_db;DB_PASSWORD=admin_password;DB_PORT=5432;DB_USERNAME=admin_user
```bash
export ADMIN_EMAIL=admin.app@email.com
export ADMIN_PASSWORD=adminapp123
export DB_HOST=localhost
export DB_NAME=jobconnect_db
export DB_PASSWORD=admin_password
export DB_PORT=5432
export DB_USERNAME=admin_user
```

4. **Ejecutar la aplicación**
```bash
./mvnw spring-boot:run
```

5. **Acceder a la documentación de la API (opcional)**
- Swagger UI: http://localhost:8080/swagger-ui.html

## Mejoras futuras
- Implementar un sistema de calificaciones y reseñas para los profesionales.
- Chat en tiempo real (WebSocket).
- Implementar un sistema de notificaciones (email, push).
- Integración con pasarelas de pago externas (Stripe, PayPal).
- Geolocalización en tiempo real.