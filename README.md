# auth-service (simple-id)

A minimal authentication service: user registration/login, JWT access tokens, rotating refresh tokens, and role-based access — built **without** Spring Security (a plain `OncePerRequestFilter` validates tokens).

## Stack

- Java 21, Spring Boot 3.3
- Maven
- H2 (file-based, for now)
- Flyway migrations
- Docker Compose
- `jjwt` for JWT, `spring-security-crypto` for BCrypt hashing only

## Run locally

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`. H2 console at `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:file:./data/authdb`, user `sa`, empty password).

## Run with Docker Compose

```bash
docker compose up --build
```

Data persists in the `auth-data` named volume.

## Test

```bash
mvn test
```

## Endpoints

| Method | Path                 | Auth            | Description                          |
|--------|----------------------|-----------------|--------------------------------------|
| POST   | `/api/auth/register` | public          | Create a user (default role `USER`)  |
| POST   | `/api/auth/login`    | public          | Returns access + refresh tokens      |
| POST   | `/api/auth/refresh`  | public          | Rotates refresh token, new pair      |
| GET    | `/api/users/me`      | Bearer token    | Current user profile                 |
| GET    | `/api/admin/users`   | Bearer + ADMIN  | List all users                       |
| GET    | `/actuator/health`   | public          | Liveness/readiness health check      |

### Example

```bash
# Register
curl -X POST localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","password":"password123"}'

# Login
curl -X POST localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","password":"password123"}'

# Access protected endpoint
curl localhost:8080/api/users/me -H "Authorization: Bearer <accessToken>"

# Refresh
curl -X POST localhost:8080/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<refreshToken>"}'
```

## Configuration

Override via environment variables (see `application.yml`):

| Variable            | Default                              | Notes                              |
|---------------------|--------------------------------------|------------------------------------|
| `DB_URL`            | `jdbc:h2:file:./data/authdb`         | Swap for Postgres later            |
| `APP_JWT_SECRET`    | dev secret (Base64)                  | **Change in production**           |
| `APP_JWT_ACCESS_TTL`| `PT15M`                              | ISO-8601 duration                  |
| `APP_JWT_REFRESH_TTL`| `P7D`                               | ISO-8601 duration                  |
| `SERVER_PORT`       | `8080`                               |                                    |

## Notes

- Schema is owned by Flyway (`src/main/resources/db/migration`); JPA runs in `ddl-auto=validate`.
- Granting `ADMIN` currently requires a DB update (e.g. via H2 console) or a future admin endpoint/migration.
- H2 is a stepping stone; the JDBC-based config makes swapping in Postgres straightforward.
