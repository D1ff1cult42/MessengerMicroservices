# 💬 Messenger Microservices



[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![gRPC](https://img.shields.io/badge/gRPC-1.60-blue?logo=grpc)](https://grpc.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

**Полнофункциональная платформа для обмена сообщениями в реальном времени**, спроектированная с нуля по принципам микросервисной архитектуры и нацеленная на **production-ready** качество.

Проект демонстрирует комплексный подход к построению высоконагруженных распределённых систем: от безопасной аутентификации с RSA-подписью JWT до потоковой передачи файлов через gRPC, от отказоустойчивой маршрутизации с Circuit Breaker до гранулярного Rate Limiting на уровне API Gateway.

### 🎯 Ключевые особенности

- **Микросервисная архитектура** — каждый домен (аутентификация, сообщения, чаты, файлы) изолирован в отдельном сервисе со своей базой данных (Database-per-Service паттерн)
- **Высокопроизводительный межсервисный транспорт** — gRPC со стримингом для передачи файлов вместо REST, Protobuf для сериализации
- **Безопасность на всех уровнях** — RSA-подпись JWT-токенов, централизованная валидация на Gateway, refresh-токены с ротацией
- **Отказоустойчивость** — Circuit Breaker (Resilience4j) с fallback-ами, health-check'и, автоматический рестарт контейнеров
- **Контейнеризация** — полная оркестрация через Docker Compose с health-check'ами и dependency-графом
- **API-first** — Swagger/OpenAPI документация для каждого сервиса
- **Чистая кодовая база** — MapStruct для маппинга, Flyway для миграций, Lombok, shared-библиотека для общего кода


## 🛠 Технологический стек

| Технология | Назначение                         |
|---|------------------------------------|
| **Java 21** | Основной язык                      |
| **Spring Boot 4.0.2** | Фреймворк                          |
| **Spring Cloud Gateway** | API Gateway с маршрутизацией       |
| **Spring Security** | Аутентификация и авторизация       |
| **Spring Data JPA** | Работа с базой данных              |
| **gRPC** | Межсервисная коммуникация (файлы)  |
| **PostgreSQL 15** | Основная база данных               |
| **Redis 7** | Кэширование                        |
| **MinIO** | S3-совместимое объектное хранилище |
| **Flyway** | Миграции базы данных               |
| **Resilience4j** | Circuit Breaker паттерн            |
| **Bucket4j + Caffeine** | Rate Limiting                      |
| **MapStruct** | Маппинг DTO ↔ Entity               |
| **Nimbus JOSE JWT** | JWT-токены (RSA)                   |
| **SpringDoc OpenAPI** | Swagger/OpenAPI документация       |
| **Docker Compose** | Оркестрация контейнеров            |
| **Lombok** | Сокращение boilerplate-кода        |
| **Gradle** | Система сборки (multi-module)      |

---

## 📦 Микросервисы

### 🔐 Auth Service (`:8081`)
Сервис аутентификации и авторизации пользователей.

- Регистрация и вход (email + пароль)
- Генерация JWT-токенов (Access + Refresh) с RSA-подписью
- Управление refresh-токенами (выдача, отзыв, ротация)
- Роли пользователей (`USER`, `ADMIN`)
- Миграции БД через Flyway

### 💬 Message Service (`:8082`)
Сервис управления сообщениями.

- CRUD-операции с сообщениями
- Поддержка типов сообщений (текст, файлы)
- Статусы доставки и прочтения (per-user)
- Ответы на сообщения (reply-to)
- Мягкое удаление сообщений
- Загрузка файлов через gRPC-клиент к File Service
- Получение presigned URL для скачивания файлов
- Пагинация

### 👥 Chat Service
Сервис управления чатами и участниками.

- Создание личных и групповых чатов
- Управление участниками чата (добавление, удаление)
- Роли участников в чате
- Иконки чатов (загрузка через gRPC → File Service → MinIO)
- Описание и метаданные чата

### 📁 File Service (`:8083` HTTP / `:9091` gRPC)
Сервис хранения и раздачи файлов.

- gRPC-сервер для загрузки файлов (стриминг чанков)
- Генерация presigned URL для безопасного скачивания
- Интеграция с MinIO (S3-compatible)
- Поддержка нескольких бакетов (аватары, вложения и т.д.)

### 🌐 API Gateway (`:8080`)
Единая точка входа для всех клиентских запросов.

- Маршрутизация запросов (Spring Cloud Gateway WebMVC)
- JWT-аутентификация (валидация RSA-подписи)
- Circuit Breaker (Resilience4j) для каждого сервиса
- Fallback-эндпоинты при недоступности сервисов
- Мониторинг через Actuator

### 📚 Common (shared-библиотека)
Общий модуль для переиспользуемого кода.

- Protobuf-определения (gRPC-контракты для File Service)
- Общие DTO и response-обёртки
- Пагинация (`PageResponse`)
- Конфигурация бакетов MinIO (auto-configuration)
- Общие исключения

---

## 🚀 Быстрый старт

### Предварительные требования

- **Docker** и **Docker Compose**
- **Java 21** (для локальной разработки)
- **Gradle 8+**

### Запуск через Docker Compose

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/D1ff1cult42/MessengerMicroservices.git
   cd MessengerMicroservices
   ```

2. Создайте файл `.env` в корне проекта:
   ```env
   # PostgreSQL
   POSTGRES_USER=messenger
   POSTGRES_PASSWORD=messenger_secret
   POSTGRES_DB=messenger_db

   # Datasources
   AUTH_DATASOURCE_URL=jdbc:postgresql://postgres:5432/auth_db
   MESSAGE_DATASOURCE_URL=jdbc:postgresql://postgres:5432/message_db

   # Redis
   REDIS_HOST=redis
   REDIS_PORT=6379
   REDIS_PASSWORD=redis_secret

   # MinIO
   MINIO_ENDPOINT=http://minio:9000
   MINIO_ACCESS_KEY=minioadmin
   MINIO_SECRET_KEY=minioadmin_secret

   # JWT (RSA ключи в формате PEM)
   JWT_PRIVATE_KEY=<your-rsa-private-key>
   JWT_PUBLIC_KEY=<your-rsa-public-key>

   # Auth
   AUTH_ISSUER=messenger-auth
   ACCESS_TOKEN_EXPIRATION=900000
   REFRESH_TOKEN_EXPIRATION=604800000
   ```

3. Запустите проект:
   ```bash
   docker compose up --build -d
   ```

4. Сервисы будут доступны:
   - **API Gateway:** http://localhost:8080
   - **Auth Service Swagger:** http://localhost:8081/swagger-ui.html
   - **Message Service Swagger:** http://localhost:8082/swagger-ui.html
   - **File Service Swagger:** http://localhost:8083/swagger-ui.html
   - **MinIO Console:** http://localhost:9001

### Локальная разработка

```bash
# Сборка всех модулей
./gradlew build

# Запуск конкретного сервиса
./gradlew :auth-service:bootRun
./gradlew :message-service:bootRun
./gradlew :file-service:bootRun
./gradlew :api-gateway:bootRun
```

---

## 🗄 Базы данных

Проект использует **PostgreSQL** с разделением на отдельные базы:

| База | Сервис | Описание |
|---|---|---|
| `auth_db` | Auth Service | Пользователи, refresh-токены |
| `message_db` | Message Service | Сообщения, статусы доставки |
| `chat_db` | Chat Service | Чаты, участники |

Инициализация баз происходит через `init-databases.sql`, миграции — через **Flyway** в каждом сервисе.

---

## 🔗 Межсервисное взаимодействие

| Источник | Назначение | Протокол | Описание |
|---|---|---|---|
| Client → API Gateway | Все сервисы | HTTP/REST | Маршрутизация запросов |
| Message Service | File Service | gRPC (streaming) | Загрузка файлов, presigned URL |
| Chat Service | File Service | gRPC (streaming) | Иконки чатов |
| API Gateway | Redis | TCP | Rate limiting, кэширование |

---

## 📋 Roadmap

### ✅ Реализовано
- [x] **Auth Service** — регистрация, JWT (Access + Refresh), RSA-подпись
- [x] **Message Service** — CRUD сообщений, статусы доставки/прочтения, файлы
- [x] **Chat Service** — личные и групповые чаты, управление участниками
- [x] **File Service** — загрузка/скачивание файлов через gRPC + MinIO
- [x] **API Gateway** — маршрутизация, JWT-фильтр, Circuit Breaker, Rate Limiting
- [x] **Common** — общая библиотека (Protobuf, DTO, пагинация, auto-config бакетов)
- [x] **Docker Compose** — полная контейнеризация всех сервисов
- [x] **Flyway** — автоматические миграции БД
- [x] **Swagger/OpenAPI** — документация API

### 🔜 В планах

- [ ] **Account Service** — профили пользователей, аватары, настройки, статус онлайн
- [ ] **Redis** — кэширование данных пользователей, сессий, чатов; pub/sub для инвалидации кэша
- [ ] **Apache Kafka** — асинхронная шина событий между сервисами (event-driven architecture)
- [ ] **WebSTOMP Notification Service** — real-time уведомления через WebSocket/STOMP (новые сообщения, статусы прочтения, уведомления о наборе текста)
- [ ] **Email Service** — отправка email (подтверждение регистрации, восстановление пароля, email-уведомления)
- [ ] **ClickHouse + Analytics Service** — сервис аналитики на базе ClickHouse (статистика сообщений, активность пользователей, метрики использования)

### 💡 Идеи на будущее

- [ ] Service Discovery (Eureka / Consul)
- [ ] Centralized Configuration (Spring Cloud Config)
- [ ] Distributed Tracing (Micrometer + Zipkin / Jaeger)
- [ ] ELK Stack / Loki для централизованного логирования
- [ ] Kubernetes-деплой (Helm-чарты)
- [ ] CI/CD пайплайн (GitHub Actions)
- [ ] End-to-end шифрование сообщений
- [ ] Голосовые и видео-звонки (WebRTC)

---

## 📁 Структура проекта

```
MessengerMicroservices/
├── api-gateway/          # API Gateway (Spring Cloud Gateway)
├── auth-service/         # Сервис аутентификации
├── chat-service/         # Сервис чатов
├── message-service/      # Сервис сообщений
├── file-service/         # Сервис файлов (HTTP + gRPC)
├── common/               # Общая библиотека (proto, DTO, utils)
├── docker-compose.yaml   # Docker Compose конфигурация
├── init-databases.sql    # SQL для инициализации БД
├── build.gradle          # Корневой Gradle конфиг
├── settings.gradle       # Multi-module настройки
└── README.md
```
---

