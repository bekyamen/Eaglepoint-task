📄 City Bus Platform – API Specification (Contract)
# City Bus Platform API Contract

## 1. Runtime Reality (Important)

- The system runs in a **local LAN environment** with a real backend (Spring Boot).
- All operations are exposed via **RESTful APIs**.
- No external APIs (SMS, email, cloud services) are used.
- All background processing (queue, scheduler) is internal.

---

## 2. API Conventions

### Base URL

/api/v1


### Data Format
- JSON request/response
- UTF-8 encoding

### ID Format
- All IDs are strings (UUID)

### Timestamp Format
- ISO 8601 (e.g., `2026-04-13T10:00:00Z`)

---

### Standard Response Format

```json
{
  "success": true,
  "data": {},
  "error": null,
  "traceId": "string"
}
Error Format
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human readable message"
  },
  "traceId": "string"
}
3. Authentication API
POST /auth/register
Request:
{
  "username": "string",
  "password": "string"
}
POST /auth/login
Request:
{
  "username": "string",
  "password": "string"
}
Response:
{
  "token": "string",
  "user": {
    "id": "string",
    "role": "PASSENGER | DISPATCHER | ADMIN"
  }
}
POST /auth/logout
4. User API
GET /users/me
GET /users (Admin only)
POST /users (Admin create dispatcher/admin)
5. Search API (Core Feature)
GET /search
Query:
?q=keyword&type=route|stop
Response:
[
  {
    "id": "string",
    "type": "route | stop",
    "name": "string",
    "score": 0.95
  }
]
GET /search/autocomplete
Query:
?q=partial
6. Route & Stop API
GET /routes
GET /routes/{id}
GET /stops
GET /stops/{id}
7. Notification API
GET /notifications
GET /notifications/unread-count
POST /notifications/{id}/read
POST /notifications/preferences
{
  "arrivalReminder": true,
  "missedAlert": true,
  "dndStart": "22:00",
  "dndEnd": "07:00"
}
8. Task & Workflow API (Dispatcher)
GET /tasks
GET /tasks/{id}
POST /tasks/{id}/approve
POST /tasks/{id}/reject
POST /tasks/{id}/return
POST /tasks/batch
{
  "taskIds": ["id1", "id2"],
  "action": "approve | reject"
}
9. Workflow API
GET /workflows/{taskId}
POST /workflows/{taskId}/transition
{
  "action": "approve | reject | return"
}
10. Data Ingestion API
POST /data/import
Upload JSON or HTML
GET /data/versions
GET /data/audit-logs
11. Data Cleaning API
GET /cleaning-rules
POST /cleaning-rules
PUT /cleaning-rules/{id}
12. Configuration API (Admin)
GET /config/sorting-weights
PUT /config/sorting-weights
{
  "frequencyWeight": 0.6,
  "popularityWeight": 0.4
}
GET /config/notification-templates
POST /config/notification-templates
13. Queue & System API (Internal)
GET /system/queue/status
{
  "pending": 120,
  "failed": 5
}
POST /system/queue/retry
14. Scheduler API (Internal)
POST /system/scheduler/run
GET /system/scheduler/status
15. Observability API
GET /health
GET /metrics
GET /logs
16. Core DTO Models
User
{
  "id": "string",
  "username": "string",
  "role": "string"
}
Route
{
  "id": "string",
  "name": "string",
  "frequencyScore": number
}
Stop
{
  "id": "string",
  "name": "string",
  "popularityScore": number
}
Notification
{
  "id": "string",
  "userId": "string",
  "type": "string",
  "content": "string",
  "status": "pending | sent | read",
  "scheduledTime": "ISO_DATE"
}
Task
{
  "id": "string",
  "type": "string",
  "status": "pending | approved | rejected | returned",
  "assignedTo": "string",
  "timeoutAt": "ISO_DATE"
}
QueueMessage
{
  "id": "string",
  "type": "string",
  "payload": {},
  "status": "pending | processing | failed",
  "retryCount": number
}
17. Error Codes
Auth
INVALID_CREDENTIALS
USER_NOT_FOUND
ACCOUNT_LOCKED
Search
INVALID_QUERY
Task
TASK_NOT_FOUND
INVALID_TRANSITION
Data
PARSE_ERROR
CLEANING_ERROR
System
QUEUE_OVERFLOW
INTERNAL_ERROR
18. Internal System Flows (Important)
Notification Flow
Event → Queue → Worker → Notification → API → UI
Data Processing Flow
Import → Parse → Clean → Store → Version → Audit Log
Workflow Execution
Task Created → Assigned → Action → State Transition → Completion
19. Security Model
Token-based authentication (local)
Role-based authorization
Password hashed using BCrypt
Sensitive data masked in responses
20. Future Integration Readiness

The API is designed to support:

microservices split
external integrations
mobile clients

Without breaking contract due to:

versioned API (/v1)
consistent DTO models
modular endpoints