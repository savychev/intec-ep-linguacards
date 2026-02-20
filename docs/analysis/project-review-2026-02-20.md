# Проектное ревью (backend + frontend)

Дата: 2026-02-20

## Короткий итог

Проект выглядит как хороший учебный MVP: есть разделение на слои, базовая JWT-аутентификация, интеграционные тесты и аккуратный Angular-клиент. Главный риск — безопасность доступа к данным через `ownerId` в API-контроллерах. Перед «боевым» использованием это нужно исправить в первую очередь.

## Сильные стороны

- Понятная структура backend: `controller` → `service` → `repository`.
- DTO используются для API в большинстве сценариев, не все сущности отдаются напрямую.
- Есть базовая обработка ошибок и валидация входных запросов.
- JWT-аутентификация собрана целостно (login/register + protected endpoints).
- Для frontend есть выделенные сервисы (`auth`, `decks`) и interceptor.
- Присутствуют интеграционные и unit-тесты для ключевых сценариев.

## Что улучшить (приоритеты)

### 1) Критично: авторизация и доступ к чужим данным (IDOR)

Сейчас в ряде endpoint'ов можно передать `ownerId` из запроса, и это создает риск горизонтального доступа к данным другого пользователя. Для защищенных endpoint'ов лучше всегда брать владельца из JWT (`/me`-подход), а `ownerId` от клиента не принимать.

### 2) Ошибки API и статусы

Сейчас часть ошибок сводится к `400 Bad Request`. Лучше разделить:

- `401` — не аутентифицирован;
- `403` — нет прав;
- `404` — сущность не найдена;
- `409` — конфликт (например duplicate);
- `422` — бизнес-валидация.

Это упростит фронтенду обработку ошибок.

### 3) Конфигурация для production

- Убрать fallback secret JWT из `application.yml` (оставить только внешние секреты).
- Отключить `ddl-auto=update` и `show-sql=true` для production-профиля.
- Добавить миграции (Flyway/Liquibase).

### 4) Производительность и поддерживаемость

- Для статистики использовать `count`-запросы вместо загрузки коллекций целиком.
- Вынести унифицированный формат ошибок (`code/message/details/path/timestamp`).
- Покрыть тестами негативные security-кейсы (подмена ownerId, invalid/expired JWT).

## Как запускать проект локально

### Требования

- Java 17
- Node.js 18+ (рекомендуется LTS)
- MySQL 8+

### 1) Поднять БД

Создай БД `linguacards` и пользователя (или используй свои значения):

```sql
CREATE DATABASE linguacards CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'lingua'@'localhost' IDENTIFIED BY 'lingua123!';
GRANT ALL PRIVILEGES ON linguacards.* TO 'lingua'@'localhost';
FLUSH PRIVILEGES;
```

### 2) Настроить переменные окружения (рекомендуется)

```bash
export DB_URL='jdbc:mysql://localhost:3306/linguacards?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC'
export DB_USERNAME='lingua'
export DB_PASSWORD='lingua123!'
export JWT_SECRET='replace-with-very-strong-secret-at-least-32-chars'
```

### 3) Запустить backend

Из корня проекта:

```bash
bash ./mvnw spring-boot:run
```

Backend будет доступен на `http://localhost:8080`.

### 4) Запустить frontend

В отдельном терминале:

```bash
cd frontend
npm install
npm start
```

Frontend будет на `http://localhost:4200`, API — `http://localhost:8080/api`.

## Быстрая проверка после запуска

1. Открыть `http://localhost:4200`.
2. Зарегистрировать пользователя.
3. Создать колоду и карточки.
4. Проверить тренировку и статистику.

## Идеи следующего шага (как учебный roadmap)

1. Убрать `ownerId` из защищенных endpoint'ов, оставить `/me`-модель.
2. Добавить централизованный error schema и корректные HTTP-статусы.
3. Подключить Flyway и сделать первую миграцию схемы.
4. Добавить пару e2e/security тестов на forbidden-сценарии.
