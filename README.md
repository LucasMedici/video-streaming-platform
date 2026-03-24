# Streamly - Video Streaming Platform Backend

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36)](https://maven.apache.org/)
[![API Docs](https://img.shields.io/badge/Docs-Swagger-85EA2D)](http://localhost:8080/docs)
[![Docker](https://img.shields.io/badge/Container-Docker-2496ED)](https://www.docker.com/)

<p align="center">
  <img src="public/streamly.png" alt="Streamly" width="220" style="border-radius: 16px;" />
</p>

Backend API for a video streaming platform with JWT authentication, user management, asynchronous video processing, and Supabase Storage integration.

## Quick Links

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Core Features](#core-features)
- [Run Locally](#run-locally)
- [Run with Docker](#run-with-docker)
- [API Documentation](#api-documentation)
- [Application Screenshots](#application-screenshots)
- [Automated Tests](#automated-tests)
- [Configuration](#configuration)

## Overview

Streamly is a Spring Boot backend that supports video upload and streaming workflows.
The project uses RabbitMQ and FFmpeg to process videos asynchronously (HLS conversion + thumbnail generation), while metadata is stored in PostgreSQL.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web / WebFlux
- Spring Data JPA
- Spring Security (JWT)
- Spring Validation
- Spring AMQP (RabbitMQ)
- PostgreSQL
- Supabase Storage
- FFmpeg / FFprobe
- MapStruct
- Swagger/OpenAPI (springdoc)
- JUnit 5 + Mockito
- Docker + Docker Compose
- Maven

## Core Features

- JWT login via `POST /auth`
- User CRUD via `/users`
- Video upload via multipart `POST /videos`
- Asynchronous processing pipeline with RabbitMQ
- HLS generation and thumbnail generation via FFmpeg
- Stream URL generation via `GET /videos/{videoId}/stream`
- Swagger UI at `/docs`

## Project Structure

```text
src/main/java/video/streaming/platform/streamly
  auth/
  config/
  exceptions/
  user/
  utils/
  video/
	processing/
  webView/

src/main/resources
  application.properties
  application-dev.properties
  static/
  templates/
```

## Security and Authorization

Current access rules from security configuration:

- Public: `POST /auth`, `POST /users`, web pages, and Swagger routes
- Admin only: `POST /videos`, `DELETE /videos/**`
- Other routes: authenticated

JWT is expected in the `Authorization` header using `Bearer <token>`.

## Run Locally

Prerequisites:

- Java 21+
- Maven 3.9+ (or use `./mvnw`)
- PostgreSQL
- RabbitMQ
- FFmpeg installed
- Supabase project credentials

Commands:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Default app URL:

- `http://localhost:8080`

## Run with Docker

This repository includes backend and RabbitMQ services in `docker-compose.yml`.

```bash
./mvnw clean package -DskipTests
docker compose build --no-cache
docker compose up -d
```

Useful URLs/ports:

- API: `http://localhost:8080`
- RabbitMQ Management: `http://localhost:15673`
- RabbitMQ AMQP (host): `localhost:5673`

## API Documentation

With the application running:

- Swagger UI: `http://localhost:8080/docs`
- OpenAPI JSON: `http://localhost:8080/api-docs`

<img src="public/docs.png" alt="Swagger Docs" style="border-radius: 16px;" />

## Application Screenshots

| Home | Login |
| --- | --- |
| <img src="public/home.png" alt="Home" style="border-radius: 16px;" /> | <img src="public/login.png" alt="Login" style="border-radius: 16px;" /> |

| Upload | Video |
| --- | --- |
| <img src="public/upload.png" alt="Upload" style="border-radius: 16px;" /> | <img src="public/video.png" alt="Video" style="border-radius: 16px;" /> |

## Automated Tests

Run all tests:

```bash
./mvnw test
```

Current test suite includes:

- `AuthControllerTest`
- `UserServiceTest`
- `VideoControllerTest`
- `VideoServiceTest`
- `JWTUtilTest`
- `GlobalExceptionHandlerTest`
- `StreamlyApplicationTests`

## Configuration

Main properties used by the application:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `supabase.url`
- `supabase.service-key`
- `supabase.bucket`
- `spring.rabbitmq.host`
- `spring.rabbitmq.port`
- `spring.rabbitmq.username`
- `spring.rabbitmq.password`
- `spring.rabbitmq.queue-video-name`
- `springdoc.swagger-ui.path`
- `springdoc.api-docs.path`
