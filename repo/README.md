# City Bus Platform - Docker Deployment

## Overview

This repository runs the complete City Bus Operation Platform in containers:

- Spring Boot backend (`/api/v1`) on port `8080`
- Angular SPA frontend via Nginx on port `80`
- PostgreSQL 16 with persistent volume on host port `55432` (container `5432`)

All services run in a private Docker network and support offline LAN execution.

## Run

```bash
docker-compose up --build
```


`docker-compose up --build` starts the full stack (postgres + backend + frontend).

## Test Commands

Run all tests via script:

```bash
./run-tests.sh
```

Run tests in Docker (optional profile):

```bash
docker-compose --profile test up --build
```

Backend tests only:

```bash
mvn -B test
```

Frontend tests only:

```bash
npm ci
npm run test -- --watch=false --browsers=ChromeHeadlessNoSandbox
```

## Tests

Backend tests:

```bash
mvn test
```

Frontend tests:

```bash
npm test
```

Optional environment overrides (recommended for production LAN):

```bash
POSTGRES_DB=citybus \
POSTGRES_USER=citybus \
POSTGRES_PASSWORD=citybus \
POSTGRES_HOST_PORT=55432 \
SECURITY_JWT_SECRET=replace-with-strong-secret \
docker-compose up --build
```

## Endpoints

- Frontend: `http://localhost`
- Backend API: `http://localhost:8080/api/v1`
- PostgreSQL: `localhost:55432` (or `${POSTGRES_HOST_PORT}`)

Frontend `/api/v1/*` requests are proxied by Nginx to `http://backend:8080/api/v1/*` over the internal Docker network (no localhost hardcoding in frontend API calls).

## Local Development (Non-Docker)

Backend:

```bash
mvn spring-boot:run
```

Frontend:

```bash
npm install
npm start
```

## Tests

Backend tests:

```bash
mvn test
```

Frontend tests:

```bash
npm test
```

## Docker Files

- Backend image: `backend/Dockerfile` (multi-stage Maven + JRE runtime)
- Frontend image: `frontend/Dockerfile` (Node build + Nginx static hosting)
- Postgres base image: `postgres/Dockerfile` (official `postgres:16`)
- Compose orchestration: `docker-compose.yml`

## Backend Docker Profile

Docker profile config is in:

- `src/main/resources/application-docker.yml`

It configures with environment variables:

- `spring.datasource.url=${SPRING_DATASOURCE_URL}`
- `spring.datasource.username=${SPRING_DATASOURCE_USERNAME}`
- `spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}`
- `server.port=${SERVER_PORT}`

## Notes

- Database data persists in Docker volume `postgres_data`.
- Backend includes health endpoint support through Spring Boot Actuator (`/actuator/health`).
- PostgreSQL readiness is enforced with health checks; backend starts after DB healthy.
- Backend connection pool uses retry-friendly Hikari settings for startup resilience.
