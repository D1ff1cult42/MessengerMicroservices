# Messenger Microservices

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.9.0-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![gRPC](https://img.shields.io/badge/gRPC-Protobuf-244c5a?logo=grpc)](https://grpc.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![Gradle](https://img.shields.io/badge/Gradle-multi--module-02303A?logo=gradle&logoColor=white)](https://gradle.org/)

Полнофункциональная платформа для обмена сообщениями в реальном времени, построенная на микросервисной архитектуре с акцентом на production-ready качество.

RSA-подписанные JWT-токены, потоковая передача файлов через gRPC, Circuit Breaker, Rate Limiting, полный стек наблюдаемости (Prometheus + Loki + Tempo + Grafana), событийная шина на Kafka, полнотекстовый поиск через Elasticsearch, реальное время через WebSocket/STOMP, аналитика на ClickHouse.

---

## Содержание

- [Ключевые особенности](#ключевые-особенности)
- [Технологический стек](#технологический-стек)
- [Архитектура](#архитектура)
- [Сервисы](#сервисы)
- [Базы данных](#базы-данных)
- [Kafka-топики](#kafka-топики)
- [gRPC-взаимодействие](#grpc-взаимодействие)
- [Наблюдаемость](#наблюдаемость)
- [Поток регистрации](#поток-регистрации)
- [Быстрый старт](#быстрый-старт)
- [Структура проекта](#структура-проекта)

---

## Ключевые особенности

- **Database-per-Service** — 6 изолированных баз PostgreSQL, каждый домен независим
- **Event-Driven Architecture** — Kafka с Avro-сериализацией и Outbox-паттерном для гарантированной доставки событий
- **gRPC стриминг** — потоковая передача файлов между сервисами без HTTP-оверхеда
- **RSA-подписанные JWT** — пара ключей RSA, публичный ключ распространяется на Gateway без передачи секрета
- **Полная наблюдаемость** — метрики (Prometheus), логи (Loki + Alloy), трейсы (Tempo + OpenTelemetry), дашборды (Grafana)
- **Отказоустойчивость** — Circuit Breaker + Time Limiter (Resilience4j) на Gateway с fallback-эндпоинтами
- **Rate Limiting** — ограничение запросов на уровне Gateway через Bucket4j + Redis per-user
- **Полнотекстовый поиск** — индексация сообщений в Elasticsearch через Kafka
- **Аналитика** — GeoIP-анализ и агрегация событий в ClickHouse
- **AsyncAPI документация** — Springwolf для WebSocket/STOMP контрактов

---

## Технологический стек

| Категория | Технология |
|---|---|
| Язык / Фреймворк | Java 21, Spring Boot 4.0.2 |
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Безопасность | Spring Security, OAuth2 Resource Server, Nimbus JOSE JWT (RSA) |
| Персистентность | Spring Data JPA, Flyway, PostgreSQL 15 |
| Кэш / Rate Limiting | Redis 7 (LRU 64MB), Bucket4j |
| Обмен сообщениями | Apache Kafka 3.9.0 (KRaft), Avro, Confluent Schema Registry 7.7.1 |
| Межсервисный транспорт | gRPC (net.devh), Protocol Buffers 3.25.3 |
| Реальное время | Spring WebSocket + STOMP |
| Поиск | Elasticsearch 9.2.3 |
| Аналитика | ClickHouse |
| Хранение файлов | MinIO (S3-compatible) |
| Отказоустойчивость | Resilience4j (Circuit Breaker, Time Limiter) |
| Наблюдаемость | OpenTelemetry, Prometheus, Grafana, Loki, Tempo, Alloy |
| GeoIP | MaxMind GeoLite2 |
| Документация API | SpringDoc OpenAPI 3.0.2, Springwolf (AsyncAPI) |
| Вспомогательные | MapStruct, Lombok, Thymeleaf (email-шаблоны) |
| Сборка / Инфра | Gradle (multi-module), Docker Compose |

---

## Архитектура

```
                              ┌─────────────────────────────────┐
                              │         Клиент (HTTP/WS)        │
                              └────────────────┬────────────────┘
                                               │ :8080
                              ┌────────────────▼────────────────┐
                              │           api-gateway            │
                              │   JWT validation · Rate Limit    │
                              │   Circuit Breaker · Routing      │
                              └──┬──────┬──────┬──────┬─────┬───┘
                                 │      │      │      │     │
             :8081               │:8082 │:8083 │:8084 │:8085│:8086..8088
         ┌───┴───┐           ┌───┴──┐ ┌┴────┐┌┴────┐┌┴───┐ └─────────┐
         │ auth  │           │ msg  │ │file ││chat ││acct│  realtime  │
         │service│           │serv. │ │serv.││serv.││serv│  +mail     │
         └───────┘           └──┬───┘ └──┬──┘└──┬──┘└────┘  +search  │
                                │  gRPC  │      │            +analytic │
                   ┌────────────┴────────┴──────┘                     │
                   │            gRPC :9091/:9092                       │
                   └────────────────────────────────────────────────── ┘
                                        │
                              ┌─────────▼─────────┐
                              │       Kafka        │
                              │  (KRaft, Avro)     │
                              └───────────────────-┘

Инфраструктура: PostgreSQL × 6 · Redis · MinIO · Elasticsearch · ClickHouse
Наблюдаемость:  Prometheus · Grafana · Loki · Tempo · Alloy
```

---

## Сервисы

### api-gateway — порт 8080

Единая точка входа для всех клиентских запросов.

- Маршрутизация к микросервисам на основе пути
- Валидация JWT по RSA-публичному ключу
- Rate Limiting per-user через Redis + Bucket4j
- Circuit Breaker + fallback-эндпоинты (Resilience4j)

| Маршрут | Сервис |
|---|---|
| `/api/auth/**` | auth-service:8081 |
| `/api/messages/**` | message-service:8082 |
| `/api/files/**` | file-service:8083 |
| `/api/chats/**` | chat-service:8084 |
| `/api/my_account/**`, `/api/accounts/**` | account-service:8085 |
| `/ws/**` | realtime-gateway:8086 (WebSocket upgrade) |
| `/api/search/**` | fulltext-search-service:8087 |
| `/api/mail/**` | mail-service:8088 |

---

### auth-service — порт 8081

Аутентификация и авторизация пользователей.

- Регистрация (email + пароль + username) — одношаговая, сразу возвращает токены
- Логин, логаут, логаут со всех устройств
- JWT Access Token (RSA-подпись) + Refresh Token (хранится в Redis)
- Подтверждение email через Kafka
- Роли: `USER`, `ADMIN`
- Outbox-паттерн для гарантированной публикации событий

**Kafka:** производит `user-registered`, `email-confirmation`, `analytic-sent` · потребляет `email-confirmed`, `account-deleted`  
**БД:** PostgreSQL (`auth_db`) + Redis

---

### account-service — порт 8085, gRPC 9092

Управление профилями пользователей.

- Аккаунт создаётся автоматически при регистрации через Kafka (`UserRegisteredEvent`)
- Обновление профиля: имя пользователя, описание, аватар
- Удаление аккаунта с публикацией события в auth-service
- Redis-кэширование с TTL 10 минут
- Загрузка аватара через gRPC → file-service

**Kafka:** производит `account-deleted` · потребляет `user-registered`  
**БД:** PostgreSQL (`account_db`) + Redis

---

### message-service — порт 8082

Управление сообщениями.

- CRUD-операции с сообщениями
- Типы сообщений: `text`, `image`, `video`, `audio`, `document`
- Статусы доставки и прочтения (per-user)
- Ответы на сообщения (reply-to)
- Мягкое удаление
- Загрузка файлов через gRPC → file-service, получение presigned URL
- Пагинация, пакетные операции (batch_size: 50)

**Kafka:** производит `message-sent`, `analytic-sent` · потребляет `message-delivered`  
**gRPC-клиенты:** file-service:9091, chat-service:9092  
**БД:** PostgreSQL (`message_db`)

---

### chat-service — порт 8084, gRPC-сервер 9092

Управление чатами и участниками.

- Создание личных и групповых чатов
- Управление участниками (добавление, удаление)
- Роли и права участников в чате
- Иконки чатов (загрузка через gRPC → file-service)
- gRPC-сервер для: message-service, realtime-gateway, fulltext-search-service

**gRPC-клиенты:** file-service:9091, account-service:9092  
**БД:** PostgreSQL (`chat_db`)

---

### file-service — порт 8083, gRPC-сервер 9091

Хранение и раздача файлов.

- gRPC-сервер: стриминговая загрузка файлов от других сервисов
- Генерация presigned URL с ограниченным временем жизни по типу бакета
- Stateless-сервис (собственная БД отсутствует)

| Бакет MinIO | Время жизни presigned URL |
|---|---|
| `message-image` | 3 часа |
| `message-video` | 1 час |
| `message-audio` | 5 часов |
| `message-document` | 6 часов |
| `account-icon` | 12 часов |
| `chat-icon` | 12 часов |

---

### realtime-gateway — порт 8086

WebSocket-шлюз для реального времени.

- WebSocket + STOMP брокер
- Доставка уведомлений о новых сообщениях подключённым клиентам
- Индикаторы набора текста (typing indicators)
- Управление жизненным циклом соединений
- Springwolf AsyncAPI-документация

**Kafka:** производит `message-delivered` · потребляет `message-sent`  
**gRPC-клиенты:** chat-service:9092  
**БД:** PostgreSQL (`realtime_db`)

---

### fulltext-search-service — порт 8087

Полнотекстовый поиск по сообщениям.

- Индексирование сообщений через Kafka (`message-sent`)
- REST-эндпоинт поиска

**Kafka:** потребляет `message-sent`  
**gRPC-клиенты:** chat-service:9092  
**БД:** Elasticsearch 9.2.3

---

### mail-service — порт 8088

Доставка email-уведомлений.

- Подтверждение email при регистрации
- HTML-шаблоны через Thymeleaf
- Управление токенами (TTL 24 часа)
- Плановая очистка каждый час

**Kafka:** производит `email-confirmed` · потребляет `email-confirmation`  
**БД:** PostgreSQL (`mail_db`)

---

### analytic-service — порт 8090

Аналитика и статистика.

- Агрегация событий от всех сервисов
- GeoIP-анализ на уровне страны (MaxMind GeoLite2)
- События аутентификации: `REGISTER`, `LOGIN`, `LOGOUT`, `LOGOUT_ALL`, `REFRESH_TOKEN`
- События сообщений: `MESSAGE_SENT`, `MESSAGE_UPDATED`, `MESSAGE_DELETED`, `MESSAGE_DELETED_BY_ADMIN`

**Kafka:** потребляет `analytic-sent`  
**БД:** ClickHouse

---

### common (общая библиотека)

- Protobuf-определения всех gRPC-контрактов
- Avro-схемы всех Kafka-событий
- Общие DTO и обёртки ответов (`PageResponse`, `ErrorResponse`)
- MinIO BucketResolver auto-configuration
- gRPC client starters

---

## Базы данных

| База данных | Сервис | Назначение |
|---|---|---|
| `auth_db` | auth-service | users, outbox_events |
| `account_db` | account-service | accounts, outbox_events |
| `message_db` | message-service | messages, statuses |
| `chat_db` | chat-service | chats, participants |
| `mail_db` | mail-service | confirmation_tokens, outbox_events |
| `realtime_db` | realtime-gateway | connections |
| Elasticsearch | fulltext-search-service | message index |
| ClickHouse | analytic-service | analytics events |
| Redis | auth-service, account-service, api-gateway | refresh tokens, cache, rate limiting |

---

## Kafka-топики

Все события сериализуются через Avro + Confluent Schema Registry. Публикация осуществляется через Outbox-паттерн: события сохраняются в таблицу `outbox_events`, `OutboxScheduler` публикует их в Kafka каждую секунду.

| Топик | Производитель | Потребитель |
|---|---|---|
| `user-registered` | auth-service | account-service |
| `email-confirmation` | auth-service | mail-service |
| `email-confirmed` | mail-service | auth-service |
| `account-deleted` | account-service | auth-service |
| `message-sent` | message-service | realtime-gateway, fulltext-search-service |
| `message-delivered` | realtime-gateway | message-service |
| `analytic-sent` | auth-service, message-service | analytic-service |

---

## gRPC-взаимодействие

| Клиент | Сервер | Порт | Назначение |
|---|---|---|---|
| message-service | file-service | 9091 | Загрузка файлов, получение presigned URL |
| message-service | chat-service | 9092 | Проверка членства в чате |
| chat-service | file-service | 9091 | Загрузка иконок чатов |
| chat-service | account-service | 9092 | Валидация участников |
| account-service | file-service | 9091 | Загрузка аватаров |
| realtime-gateway | chat-service | 9092 | Информация о чате для WebSocket-сессий |
| fulltext-search-service | chat-service | 9092 | Метаданные чата для индексации |

---

## Наблюдаемость

| Компонент | Порт | Назначение |
|---|---|---|
| Prometheus | 9090 | Сбор метрик (все сервисы + экспортёры) |
| Grafana | 3000 | Дашборды |
| Loki | 3100 | Агрегация логов (retention 168h) |
| Tempo | 3200 | Распределённая трассировка (OTLP) |
| Alloy | 12345 | Форвардинг логов из Docker-контейнеров |
| Kafka UI | 8089 | Визуализация Kafka-кластера |

**Экспортёры метрик:** postgres-exporter (9187), redis-exporter (9121), kafka-exporter (9308), elasticsearch-exporter (9114)

Все сервисы экспортируют метрики на `/actuator/prometheus` с интервалом скрейпинга 10 секунд. OpenTelemetry OTLP-трейсы отправляются в Tempo с частотой семплирования 100%. Alloy парсит JSON-логи контейнеров, извлекает `trace_id`, `span_id`, `service`, `level` и форвардит в Loki.

---

## Поток регистрации

```
1. Клиент     POST /api/auth/register { email, password, username }
                      │
2. auth-service       ├─ создаёт User в auth_db
                      ├─ сохраняет UserRegisteredEvent в outbox_events
                      └─ возвращает Access Token + Refresh Token немедленно

3. OutboxScheduler (каждые 1с)
                      └─ публикует UserRegisteredEvent → Kafka [user-registered]

4. account-service    └─ потребляет UserRegisteredEvent → создаёт Account в account_db

5. mail-service (асинхронно)
                      └─ получает email-confirmation → отправляет письмо с токеном подтверждения
```

---

## Быстрый старт

**Требования:** Docker, Docker Compose

### 1. Клонирование репозитория

```bash
git clone https://github.com/your-username/MessengerMicroservices.git
cd MessengerMicroservices
```

### 2. Создание файла `.env`

Создайте файл `.env` в корне проекта:

```env
# ===================================================================
# MessengerMicroservices — Environment Variables (Example)
# ===================================================================

# ─── PostgreSQL ─────────────────────────────────────────────────────
POSTGRES_USER=messenger
POSTGRES_PASSWORD=<your-postgres-password>
POSTGRES_DB=messenger

# ─── Datasource URLs (JDBC) ────────────────────────────────────────
AUTH_DATASOURCE_URL=jdbc:postgresql://postgres:5432/auth_db
MESSAGE_DATASOURCE_URL=jdbc:postgresql://postgres:5432/message_db
CHAT_DATASOURCE_URL=jdbc:postgresql://postgres:5432/chat_db
ACCOUNT_DATASOURCE_URL=jdbc:postgresql://postgres:5432/account_db
MAIL_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mail_db
REALTIME_DATASOURCE_URL=jdbc:postgresql://postgres:5432/realtime_db

# ─── Redis ──────────────────────────────────────────────────────────
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=<your-redis-password>
CACHE_ACCOUNT_TTL=10m

# ─── MinIO (S3-совместимое хранилище) ───────────────────────────────
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_PUBLIC_ENDPOINT=http://localhost:9000
MINIO_SECRET_KEY=<your-minio-secret>

# ─── Kafka ──────────────────────────────────────────────────────────
KAFKA_BOOTSTRAP_SERVERS=kafka:29092
SCHEMA_REGISTRY_URL=http://schema-registry:8081

# ─── JWT (RSA-2048 ключи) ──────────────────────────────────────────
# Сгенерировать:
#   openssl genpkey -algorithm RSA -outform PEM -pkeyopt rsa_keygen_bits:2048 > private.pem
#   openssl rsa -in private.pem -pubout -outform PEM > public.pem
# Вставить содержимое (PEM-формат), заменив переносы строк на \n
JWT_PRIVATE_KEY=<your-rsa-private-key-pem>
JWT_PUBLIC_KEY=<your-rsa-public-key-pem>

# ─── Auth Service ───────────────────────────────────────────────────
AUTH_ISSUER=messenger-auth
ACCESS_TOKEN_EXPIRATION=15m
REFRESH_TOKEN_EXPIRATION=30d

# ─── Mail Service ───────────────────────────────────────────────────
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<your-email@gmail.com>
MAIL_PASSWORD=<your-app-password>
MAIL_CONNECTION_TIMEOUT=15000
MAIL_READ_TIMEOUT=15000
MAIL_WRITE_TIMEOUT=15000
MAIL_HEALTH_ENABLED=false
MAIL_TOKEN_EXPIRATION=24h
MAIL_CONFIRM_URL=http://localhost:8080/api/mail/confirm

# ─── Analytic Service ────────────────────────────────────────────────
CLICKHOUSE_HOST=clickhouse
CLICKHOUSE_DB=analytics
CLICKHOUSE_USER=default
CLICKHOUSE_PASSWORD=<your-clickhouse-password>
```

> **Генерация RSA-ключей:**
> ```bash
> openssl genrsa -out private.pem 2048
> openssl rsa -in private.pem -pubout -out public.pem
> ```

### 3. Запуск

```bash
docker compose up --build -d
```

### 4. Доступные адреса

| Сервис | Адрес |
|---|---|
| API Gateway | http://localhost:8080 |
| Swagger (auth) | http://localhost:8081/swagger-ui.html |
| Swagger (messages) | http://localhost:8082/swagger-ui.html |
| Swagger (files) | http://localhost:8083/swagger-ui.html |
| Swagger (chats) | http://localhost:8084/swagger-ui.html |
| Swagger (accounts) | http://localhost:8085/swagger-ui.html |
| AsyncAPI (realtime) | http://localhost:8086/asyncapi-ui.html |
| Swagger (search) | http://localhost:8087/swagger-ui.html |
| MinIO Console | http://localhost:9001 |
| Kafka UI | http://localhost:8089 |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |

---

## Структура проекта

```
MessengerMicroservices/
├── api-gateway/                # API Gateway (Spring Cloud Gateway)
├── auth-service/               # Аутентификация и авторизация
├── account-service/            # Управление профилями
├── message-service/            # Управление сообщениями
├── chat-service/               # Управление чатами
├── file-service/               # Хранение файлов (HTTP + gRPC)
├── realtime-gateway/           # WebSocket/STOMP шлюз
├── fulltext-search-service/    # Полнотекстовый поиск (Elasticsearch)
├── mail-service/               # Email-уведомления
├── analytic-service/           # Аналитика (ClickHouse)
├── common/                     # Общая библиотека (proto, Avro, DTO, utils)
├── observability/              # Конфигурации Prometheus, Grafana, Loki, Tempo, Alloy
├── geoip/                      # MaxMind GeoLite2 база данных
├── postman/                    # Postman-коллекции
├── docker-compose.yaml         # Оркестрация всех контейнеров
├── init-databases.sql          # Инициализация PostgreSQL баз данных
├── clickhouse-init.sql         # Инициализация ClickHouse схемы
├── build.gradle                # Корневой Gradle конфиг
├── settings.gradle             # Multi-module настройки
└── README.md
```
