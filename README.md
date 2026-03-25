# Music Catalog API

<p align="center">
  Аккуратный REST API для управления музыкальным каталогом: артистами, альбомами, треками, жанрами, плейлистами и пользователями.
</p>

<p align="center">
  <code>Java 21</code>
  <code>Spring Boot 3.3.4</code>
  <code>PostgreSQL 17</code>
  <code>Flyway</code>
  <code>Swagger UI</code>
</p>

## Что умеет проект

- CRUD-операции для `artists`, `albums`, `tracks`, `genres`, `playlists`, `users`
- Поиск альбомов по фильтрам `title`, `artistName`, `genreName`, `yearFrom`, `yearTo`
- Два варианта поиска: через `JPQL` и через `native SQL`
- Пагинация результатов поиска через `Pageable`
- Единый формат ошибок для валидации, некорректного JSON, конфликтов и `404`
- Миграции схемы через Flyway
- Документация OpenAPI и интерактивный Swagger UI
- Логирование времени выполнения сервисных методов через AOP

## Стек

| Слой | Технологии |
| --- | --- |
| Backend | Java 21, Spring Boot 3.3.4 |
| Web | Spring Web |
| Data | Spring Data JPA, Hibernate |
| Database | PostgreSQL 17 |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation |
| Docs | springdoc-openapi, Swagger UI |
| Utility | Lombok, Maven Wrapper |

## Архитектура

Проект организован по классической структуре Spring-приложения:

```text
src/main/java/com/example/musiccatalog
|- aspect/        # AOP-логирование времени выполнения сервисов
|- config/        # OpenAPI-конфигурация
|- controller/    # REST-контроллеры
|- dto/           # Контракты API
|- entity/        # JPA-сущности
|- exception/     # Доменные исключения
|- handler/       # Унифицированная обработка ошибок
|- mapper/        # Преобразование entity <-> dto
|- repository/    # JPA-репозитории и поисковые запросы
\- service/       # Бизнес-логика и кэш индекса поиска

src/main/resources
|- application.yml
|- db/migration/  # Flyway-миграции
\- logback-spring.xml
```

## Быстрый старт

### 1. Поднять PostgreSQL

```bash
docker compose up -d
```

`docker-compose.yml` поднимает только PostgreSQL. Само Spring Boot приложение запускается отдельно через Maven Wrapper.

По умолчанию база поднимается со следующими параметрами:

| Параметр | Значение |
| --- | --- |
| Host | `localhost` |
| Port | `5432` |
| DB | `music_catalog` |
| User | `postgres` |
| Password | `1234` |

### 2. Запустить приложение

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

Приложение стартует на `http://localhost:8087`.

### 3. Открыть документацию API

- Swagger UI: [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8087/v3/api-docs](http://localhost:8087/v3/api-docs)

## Конфигурация

Основные настройки лежат в `src/main/resources/application.yml`.

Можно переопределить логин и пароль БД через переменные окружения:

```bash
DB_USERNAME=postgres
DB_PASSWORD=1234
```

Ключевые значения по умолчанию:

| Настройка | Значение |
| --- | --- |
| HTTP port | `8087` |
| `spring.jpa.hibernate.ddl-auto` | `validate` |
| Flyway | включен |
| Логи приложения | каталог `logs/` |

## Карта API

| Ресурс | Endpoint |
| --- | --- |
| Artists | `/api/artists` |
| Albums | `/api/albums` |
| Tracks | `/api/tracks` |
| Genres | `/api/genres` |
| Playlists | `/api/playlists` |
| Users | `/api/users` |

Для всех ресурсов доступны базовые операции:

- `GET /api/{resource}`
- `GET /api/{resource}/{id}`
- `POST /api/{resource}`
- `PUT /api/{resource}/{id}`
- `DELETE /api/{resource}/{id}`

Дополнительные endpoints для альбомов:

- `GET /api/albums/search/jpql`
- `GET /api/albums/search/native`

Оба search-endpoint поддерживают:

- фильтры `title`, `artistName`, `genreName`, `yearFrom`, `yearTo`
- пагинацию через `page`, `size`
- кэширование результатов в памяти

Кэш поиска инвалидируется при `create`, `update` и `delete` альбомов.

## Примеры запросов

### Создать альбом

```http
POST /api/albums
Content-Type: application/json
```

```json
{
  "title": "Master of Puppets",
  "year": 1986,
  "artistId": 1,
  "genreIds": [1, 2],
  "tracks": []
}
```

### Найти альбомы через JPQL

```http
GET /api/albums/search/jpql?artistName=Metallica&genreName=Metal&yearFrom=1980&yearTo=1990&page=0&size=10
```

### Найти альбомы через native SQL

```http
GET /api/albums/search/native?title=master&page=0&size=10
```

## Формат ошибок

API возвращает единый JSON-ответ при ошибках:

```json
{
  "timestamp": "2026-03-22T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/artists",
  "method": "POST",
  "validationErrors": [
    {
      "field": "name",
      "message": "must not be blank",
      "rejectedValue": ""
    }
  ]
}
```

Обработаны основные сценарии:

- `VALIDATION_ERROR`
- `MALFORMED_REQUEST`
- `BAD_REQUEST`
- `NOT_FOUND`
- `DATA_INTEGRITY_VIOLATION`
- `INTERNAL_ERROR`

## Полезные команды

Запуск тестов:

```powershell
.\mvnw.cmd test
```

Сборка без тестов:

```powershell
.\mvnw.cmd -DskipTests clean compile
```

Поднять только БД:

```bash
docker compose up -d postgres
```

Остановить контейнеры:

```bash
docker compose down
```

## Что стоит посмотреть в коде

- `AlbumController` и `AlbumService` для логики поиска и пагинации
- `AlbumRepository` для `JPQL` и `native SQL` запросов
- `AlbumSearchIndex` для простого in-memory кэша
- `GlobalExceptionHandler` для унификации ошибок API
- `db/migration` для эволюции схемы базы данных

## Тесты

В проекте уже есть тесты для важных частей поведения:

- корректность ключей и кэша поиска альбомов
- унифицированный формат ошибок и обработка невалидных запросов

---

Если нужен следующий шаг, логично добавить в репозиторий Postman-коллекцию или `docs/` с диаграммой сущностей и примерами сценариев.
