# Fire Dispatch API

Служба приёма вызовов пожарной охраны — «911 для пожарных».
Диспетчер принимает вызов, вызов попадает в базу со статусом `NEW`, затем на него
назначают бригаду и ведут по статусам до закрытия.

Стек: Spring Boot + PostgreSQL, всё поднимается одной командой через Docker Compose.

## Запуск

```bash
cp .env.example .env
docker compose up --build
```

Сервисы: `app` (Spring Boot, порт `APP_PORT`, по умолчанию 8080) и `db` (postgres:17-alpine).
Общаются по внутренней сети `backend` по имени сервиса — `jdbc:postgresql://db:5432/...`.
Данные лежат в named volume `pgdata`, поэтому переживают `docker compose down`.

## Переменные окружения (`.env`)

| Переменная    | Назначение               | Default   |
|---------------|--------------------------|-----------|
| `DB_NAME`     | имя БД                   | firecalls |
| `DB_USER`     | пользователь БД          | firecalls |
| `DB_PASSWORD` | пароль БД                | firecalls |
| `APP_PORT`    | порт приложения на хосте | 8080      |

## Структура проекта

```
src/main/java/kz/hw1/
├── Hw1Application.java          точка входа
├── controller/FireCallController.java   HTTP-слой: маппинг запросов
├── service/FireCallService.java         бизнес-логика и валидация
├── repository/FireCallRepository.java   доступ к БД (Spring Data JPA)
├── entity/FireCall.java, CallStatus.java   модель и статусы
└── dto/CreateCallRequest.java, UpdateCallRequest.java   тела запросов
```

## Модель вызова

| Поле          | Тип        | Описание                                         |
|---------------|------------|--------------------------------------------------|
| `id`          | number     | идентификатор вызова                             |
| `address`     | string     | куда ехать — **обязателен**                      |
| `callerPhone` | string     | телефон вызывающего                              |
| `description` | string     | что горит со слов вызывающего                    |
| `status`      | enum       | `NEW` → `DISPATCHED` → `ON_SCENE` → `CLOSED`     |
| `brigade`     | string     | назначенная бригада, пока не назначена — `null`  |
| `createdAt`   | timestamp  | время приёма вызова, ставится сервером           |

```json
{
  "id": 1,
  "address": "Abay 10, кв. 25",
  "callerPhone": "+7 777 123 45 67",
  "description": "Горит проводка на кухне, сильный дым",
  "status": "DISPATCHED",
  "brigade": "ПЧ-4, расчёт 2",
  "createdAt": "2026-10-30T12:00:00Z"
}
```

## API

Base URL: `http://localhost:8080/api/calls`, формат — JSON.

| Метод  | URL               | Что делает                              | Код ответа           |
|--------|-------------------|-----------------------------------------|----------------------|
| POST   | `/api/calls`      | принять вызов (статус `NEW`)            | 201 Created / 400    |
| GET    | `/api/calls`      | список вызовов (`?status=NEW` — фильтр) | 200 OK               |
| GET    | `/api/calls/{id}` | карточка одного вызова                  | 200 OK / 404         |
| PUT    | `/api/calls/{id}` | обновить карточку, назначить бригаду    | 200 OK / 400 / 404   |
| DELETE | `/api/calls/{id}` | удалить вызов                           | 204 No Content / 404 |

**POST** — тело: `address` (обязателен), `callerPhone`, `description`.
Статус всегда `NEW`, `createdAt` ставит сервер.

**PUT** — полная замена карточки: `address` (обязателен), `callerPhone`, `description`,
`status`, `brigade`. Если `status` не передан — остаётся прежний.

**GET** `?status=` — фильтр по `NEW` | `DISPATCHED` | `ON_SCENE` | `CLOSED`.

```bash
# принять вызов
curl -X POST localhost:8080/api/calls -H 'Content-Type: application/json' \
  -d '{"address":"Abay 10, кв. 25","callerPhone":"+7 777 123 45 67","description":"Горит проводка"}'

# что сейчас в работе
curl localhost:8080/api/calls
curl 'localhost:8080/api/calls?status=NEW'
curl localhost:8080/api/calls/1

# выслать бригаду
curl -X PUT localhost:8080/api/calls/1 -H 'Content-Type: application/json' \
  -d '{"address":"Abay 10, кв. 25","status":"DISPATCHED","brigade":"ПЧ-4, расчёт 2"}'

curl -X DELETE localhost:8080/api/calls/1
```

## Postman

`postman_collection.json` в корне репозитория — импортировать в Postman (Import → File).
Переменная `baseUrl` = `http://localhost:8080`; запрос «Create call» сохраняет `id` в `callId`,
поэтому остальные запросы работают сразу после него.

## Данные сохраняются

```bash
docker compose down && docker compose up   # данные на месте
docker compose down -v                     # volume удалён, база чистая
```
