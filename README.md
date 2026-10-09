# Fire Alarm API

Spring Boot + PostgreSQL, Docker Compose.
Датчики в комнатах отправляют температуру, бэкенд сам выставляет статус `OK` / `WARNING` / `FIRE`.
Порог задаётся через env `ALARM_FIRE_TEMP` (>= порога → FIRE, >= 80% → WARNING).

## Запуск

```bash
cp .env.example .env
docker compose up --build
```

## API

| Метод | URL                | Что делает                                                              |
|-------|--------------------|-------------------------------------------------------------------------|
| POST  | `/api/alarms`      | создать датчик `{"address": "Abay 10, kitchen"}` (address необязателен) |
| GET   | `/api/alarms`      | список всех (`?status=FIRE` — только горящие)                           |
| GET   | `/api/alarms/{id}` | один датчик                                                             |
| PUT   | `/api/alarms/{id}` | новое показание `{"temperature": 85}` → статус пересчитывается          |

```bash
curl -X POST localhost:8080/api/alarms -H 'Content-Type: application/json' -d '{"address":"Abay 10, kitchen"}'
curl -X PUT localhost:8080/api/alarms/1 -H 'Content-Type: application/json' -d '{"temperature":85}'
curl 'localhost:8080/api/alarms?status=FIRE'
```

## Данные сохраняются

```bash
docker compose down && docker compose up 
docker compose down -v                   
```
