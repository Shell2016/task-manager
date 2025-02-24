# Task-manager

Для запуска приложения понадобится запущенный постгрес
(можно воспользоваться compose.yml)

Не стал добавлять OpenApi, чтобы не загромождать контроллер аннотациями.

Краткое описание API:

Порт 8080

POST /tasks — создание новой задачи.
  Пример валидного запроса:
```json
{
"title": "someTitle",
"description": "blablabla",
"userId": "1"    
}
```
GET /tasks/{id} — получение задачи по ID.

PUT /tasks/{id} — обновление задачи.
  Пример валидного запроса:
```json
{
  "title": "newTitle",
  "description": "newDesc"
}
```
DELETE /tasks/{id} — удаление задачи.

GET /tasks — получение списка всех задач.