# Backend code review (MVP, pragmatic)

## 1) Архитектура

- **Слои в целом соблюдены**: контроллеры тонкие, основная логика в `service`, доступ к БД через `repository`, DTO используются для request/response в большинстве API-потоков.
- **UML vs реализация**:
  - Доменная модель `User -> Deck -> Card -> ReviewLog` совпадает с class diagram.
  - Основные use-cases (auth, decks/cards CRUD, training/review, stats) реализованы.
  - `Logout` заявлен в документации/use-case, но endpoint отсутствует (для stateless JWT это допустимо, но должен быть отражён как «client-side token discard»).
- **Архитектурные smell'ы**:
  - Параллельное существование `/me/...` и «обычных» endpoint'ов с `ownerId` усложняет контракт API и создаёт риск ошибок авторизации.
  - В `StatsController` наружу возвращается сервисная модель `DeckStats` напрямую (лучше через API DTO для стабильности контракта).
  - `IllegalArgumentException` используется как универсальная доменная/валидационная/авторизационная ошибка — затрудняет поддержку.
- **Масштабируемость**: для MVP нормально; при росте нагрузки первыми узкими местами станут подсчёты статистики и отсутствие явной стратегии транзакций/конкурентного доступа.

## 2) Security

- **JWT базово корректен**: stateless, `exp`/`iat`/`iss`/`sub` есть, HMAC signing, интеграция через resource-server.
- **Критичный риск IDOR / horizontal privilege escalation**:
  - В контроллерах `ownerId` из query-параметра имеет приоритет над пользователем из токена (`resolveOwnerId`).
  - Это позволяет аутентифицированному пользователю обращаться к чужим данным, если известны `ownerId` + идентификаторы сущностей.
  - Для MVP нужно минимум: игнорировать `ownerId` для authenticated-запросов (или жёстко проверять `ownerId == currentUserId`).
- **SecurityContext usage**: `CurrentUserService` корректно извлекает `sub` из JWT, но каждый вызов `getCurrentUserId()` делает lookup в БД (доп. накладные расходы и возможные race между токеном и БД).
- **JWT hardening**:
  - Нет проверки `issuer` на стороне decoder (claim добавляется при выдаче, но не валидируется при приёме).
  - Нет `aud`/`nbf` политики.
- **CORS**: для dev-сценария приемлемо (single origin, credentials enabled).
- **Пароли**: BCrypt используется корректно.
- **Production-minimum**:
  - убрать default-secret из `application.yml`, только через env/secret manager;
  - ротация ключа и раздельные ключи по окружениям;
  - нормализованные 401/403/404 ответы вместо 400 для security/ownership кейсов.

## 3) API Design

- **REST в целом ок**, ресурсы читаемые, иерархия deck/card логична.
- **HTTP-коды**:
  - Сейчас почти все доменные ошибки уходят в `400 Bad Request`.
  - Для стабильного frontend-контракта лучше различать: `401` (auth), `403` (forbidden), `404` (not found), `409` (duplicate), `422` (validation/business rule).
- **DTO usage**:
  - Плюс: request/response DTO применяются.
  - Минус: статистика возвращается не через API DTO.
- **Ошибки/обработка**:
  - Есть единый `ApiExceptionHandler`, но покрытие исключений узкое (нет `MethodArgumentNotValidException`, `AccessDeniedException`, JWT-specific exception mapping).
  - Формат ошибок минималистичен; фронту удобнее иметь единый error schema (`code`, `message`, `details`, `timestamp`, `path`).
- **Стабильность API для frontend**: на MVP приемлемо, но неоднозначность `ownerId` + `/me` создаёт риск непредсказуемого поведения при интеграции.

## 4) Data layer

- **JPA mapping**: корректный минимальный набор; связи `ManyToOne(fetch = LAZY)` выбраны разумно.
- **Ограничения БД**:
  - Есть unique по email и `(deck_id, term)` — хорошо.
  - Проверка «duplicate term» в сервисе + DB unique = правильная двухуровневая защита.
- **Риски lazy/eager**: критичных нет, т.к. контроллеры не сериализуют сущности напрямую.
- **Потенциальные N+1/эффективность**:
  - В `StatsService` totalCards считается через `findAllByDeckId(...).size()` вместо `countByDeckId`.
  - Для больших deck’ов это лишняя загрузка данных.
- **Валидация**:
  - Входные DTO валидируются.
  - Нет явных DB CHECK-constraint’ов для enum-like полей (`cefrLevel`) и бизнес-диапазонов.

## 5) Тестирование

- **Плюсы**:
  - Есть интеграционные smoke/e2e-like сценарии через MockMvc.
  - Есть тесты JWT `/me` flow и fallback без `ownerId`.
  - Есть unit test для `CurrentUserService`.
- **Критично не покрыто**:
  - Негативные security-кейсы с подменой `ownerId` при включённой auth (это текущий high-risk пробел).
  - Контракты ошибок (валидатор, 401/403/404/409).
  - Границы JWT (expired token, wrong issuer, malformed token).
- **Для production readiness добавить минимум**:
  - security integration tests на ownership/forbidden;
  - tests на exception mapping и validation payload;
  - repository/service test на производительность статистики (count vs load).

## 6) Production readiness

### Обязательно до production
1. Закрыть IDOR-риск вокруг `ownerId` (жёсткая привязка к current user).
2. Вынести JWT secret из дефолтного конфига, включить env-only secrets.
3. Пересобрать exception mapping с корректными HTTP статусами и единым error contract.
4. Отключить `ddl-auto=update` и `show-sql=true` в production-профиле; перейти на миграции (Flyway/Liquibase).

### Можно отложить
- Refresh token / revoke-list (если TTL короткий и риск приемлем для MVP).
- Расширенные метрики/трассировка и rate limiting.
- Оптимизация статистики в отдельную агрегированную модель.

### Минимальный hardening checklist
- [ ] `app.security.jwt.secret` только из внешнего секрета.
- [ ] Проверка `issuer` (и при необходимости `aud`) в decoder.
- [ ] Ownership enforcement без `ownerId` из клиента.
- [ ] Стандартизованный error response + корректные статусы.
- [ ] Production profile: без debug SQL, без auto-ddl update.
- [ ] Набор security regression tests в CI.
