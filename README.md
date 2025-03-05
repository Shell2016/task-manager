# Task-manager

Для работы EmailNotificationService понадобятся следующие енвы:
SPRING_MAIL_PASSWORD  
SPRING_MAIL_USERNAME (настроено для работы с Google Mail)  
SPRING_MAIL_RECIPIENT  
SPRING_MAIL_FROM

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
  "description": "newDesc",
  "status": "IN_PROGRESS"
}
```
DELETE /tasks/{id} — удаление задачи.

GET /tasks — получение списка всех задач.