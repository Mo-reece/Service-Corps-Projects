# Role-Based Access Control with JWT

A stateless Spring Boot REST API secured with **JWT authentication** and **role-based access control**.
Users log in, receive a signed token, and send it as `Authorization: Bearer <TOKEN>`. Every request is then
checked against the user's role (`ADMIN`, `USER` or `GUEST`). Users and roles are stored in **PostgreSQL**.

## 🎥 Video walkthrough

**▶ [Watch the project 1 walkthrough](https://www.youtube.com/watch?v=wfEX4y6hwto)**

## Features

- JWT login with signed (HS256), one-hour tokens
- Three roles with different permissions, enforced on every request
- Passwords hashed with BCrypt and never returned by the API
- Input validation with clear error messages (400 / 401 / 403 / 404 / 409)
- One command to run the whole stack with Docker

## Tech stack

Java 21 · Spring Boot 3.5 · Spring Security (OAuth2 Resource Server) · Spring Data JPA · PostgreSQL 16 · Docker

## Quick start

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
docker compose up --build
```

The API runs at `http://localhost:8080`. One demo account per role is created on startup:

| Username | Password   | Role  |
|----------|------------|-------|
| `admin`  | `admin123` | ADMIN |
| `user`   | `user123`  | USER  |
| `guest`  | `guest123` | GUEST |

<details>
<summary>Run without Docker</summary>

Requires Java 21, Maven and a PostgreSQL database named `rbac` on `localhost:5432` (user and password `postgres`).

```bash
mvn spring-boot:run
```
</details>

## Access rules

| Endpoint              | ADMIN | USER | GUEST | No token |
|-----------------------|:-----:|:----:|:-----:|:--------:|
| `POST /auth/register` | ✅ | ✅ | ✅ | ✅ |
| `POST /auth/login`    | ✅ | ✅ | ✅ | ✅ |
| `GET /users/me`       | ✅ | ✅ | ✅ | 401 |
| `PUT /users/me`       | ✅ | ✅ | 403 | 401 |
| `GET /users`          | ✅ | 403 | 403 | 401 |
| `DELETE /users/{id}`  | ✅ | 403 | 403 | 401 |

New accounts from `/auth/register` always get the `USER` role, so nobody can sign themselves up as an admin.

## Usage

```bash
# Log in and copy the token from the response: {"token":"eyJ..."}
curl -X POST localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Call protected endpoints with the token
curl localhost:8080/users -H "Authorization: Bearer <TOKEN>"
curl -X DELETE localhost:8080/users/3 -H "Authorization: Bearer <TOKEN>"

# Update your own profile (ADMIN or USER)
curl -X PUT localhost:8080/users/me \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"email":"me@example.com"}'

# Register a new account
curl -X POST localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret"}'
```

## Project structure

```
src/main/java/com/example/rbac/
├── RbacApplication.java   # Entry point; seeds one demo account per role
├── SecurityConfig.java    # Access rules, JWT signing/verification, BCrypt
├── AuthController.java    # POST /auth/register, POST /auth/login
├── UserController.java    # /users endpoints (business logic only)
├── User.java              # JPA entity + Role enum -> "users" table
└── UserRepository.java    # Spring Data repository
src/main/resources/
└── application.yml        # Database connection and JWT secret
```

## Design decisions

- **Spring Security's built-in JWT support, not a custom filter.** The OAuth2 Resource Server reads the Bearer header
  and validates the signature and expiry. Less hand-written security code means fewer places for bugs.
- **Every access rule lives in one place** (`SecurityConfig`). Controllers contain only business logic.
- **Roles travel inside the token.** A small converter maps the `role` claim to Spring's `ROLE_` authorities,
  so `hasRole("ADMIN")` works without a database lookup on every request.
- **Same error for a wrong username or a wrong password**, so attackers can't find out which usernames exist.
- **The signing secret comes from config.** It's overridable with the `JWT_SECRET` environment variable,
  so production keys never live in the code.

## Testing

Verified end to end against PostgreSQL in Docker with 22 checks. These cover every endpoint × role combination
in the table above, plus missing/invalid tokens, wrong passwords, duplicate usernames, blank and invalid input,
and deleting a user that doesn't exist. Responses never include password hashes.
