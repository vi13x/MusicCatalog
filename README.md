# Music Catalog

REST API каталога музыкальных композиций: исполнители, альбомы, треки, жанры и плейлисты. Реализованы CRUD-операции, связи OneToMany и ManyToMany, оптимизация запросов (EntityGraph, решение N+1) и демонстрация транзакционности.

---

## Стек

- **Java 21** · **Spring Boot 3.3**
- **Spring Data JPA** · **Hibernate** · **PostgreSQL 17**
- **Flyway** · **Lombok** · **Bean Validation**

---

## Запуск

**1. База данных (Docker):**

```bash
docker compose up -d
```

**2. Приложение:**

```bash
./mvnw spring-boot:run
```

Сервис доступен по адресу: `http://localhost:8080`

---

## API

Базовый префикс: `/api`

| Ресурс      | Описание                          |
|------------|------------------------------------|
| `/artists` | Исполнители (CRUD)                 |
| `/albums`  | Альбомы с треками и жанрами (CRUD) |
| `/tracks`  | Треки (CRUD)                       |
| `/genres`  | Жанры (CRUD)                       |
| `/playlists` | Плейлисты (CRUD)                |
| `/demo/nplus1` | Демо N+1 (POST, body: `[1,2,3]`) |
| `/demo/nplus1-fixed` | Решение N+1 через EntityGraph |
| `/demo/no-tx` | Демо без транзакции (GET)      |
| `/demo/tx` | Демо с @Transactional (GET)         |

Примеры: `GET /api/artists`, `GET /api/albums/1`, `POST /api/artists` с телом `{"name": "Artist Name"}`.

---

## Структура БД

- **users** — пользователи  
- **artists** — исполнители  
- **genres** — жанры  
- **albums** — альбомы (FK: artist_id)  
- **tracks** — треки (FK: album_id)  
- **playlists** — плейлисты (FK: user_id, опционально)  
- **album_genres** — связь альбом ↔ жанр (N:M)  
- **playlist_tracks** — связь плейлист ↔ трек (N:M)

Схема создаётся и обновляется через **Flyway** (`src/main/resources/db/migration`).

---

## Качество кода

Статический анализ и метрики: **[SonarCloud](https://sonarcloud.io/projects?security=1&security_review=1)**.
