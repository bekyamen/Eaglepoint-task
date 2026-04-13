# design.md

## 1. System Overview

The City Bus Operation and Service Coordination Platform is an internal enterprise system designed to manage public transport operations within a closed local-area network (LAN).

Primary roles:

* Passenger
* Dispatcher
* Administrator

Core capabilities:

* intelligent bus route and stop search (keyword + pinyin + initials)
* notification system with reminders and message center
* dispatcher workflow management (approvals, reviews, escalation)
* configurable system rules (sorting weights, templates, cleaning rules)
* structured data ingestion and cleaning pipeline
* internal message queue and scheduler system
* observability (logging, metrics, tracing)

The system is deployed fully offline and does not rely on external APIs or cloud services.

---

## 2. Design Goals

* Fully offline LAN operation
* Clear separation of concerns (UI, API, business logic, persistence)
* High performance search (<300ms target)
* Reliable workflow execution and task tracking
* Configurable system behavior without redeployment
* Strong observability and diagnostics
* Scalable architecture for city-level data

---

## 3. High-Level Architecture

```text
Angular Frontend (SPA)
        ↓
REST API Layer (Spring Boot Controllers)
        ↓
Application Services Layer
        ↓
Domain Layer (Business Logic)
        ↓
Repository Layer (JPA / PostgreSQL)
        ↓
PostgreSQL Database

+ Supporting Systems:
  - Message Queue
  - Scheduler
  - Workflow Engine
  - Data Processing Engine
  - Observability Stack


Architecture Principle
Backend is the single source of truth
Frontend contains no business logic
All workflows executed in backend services
4. Modular Backend Architecture

The backend follows a modular monolith structure.

Core Modules
Auth Module
Search Module
Notification Module
Workflow Module
Task Management Module
Data Ingestion Module
Data Cleaning Module
Config Management Module
Observability Module

Each module contains:

Controller
Service
Domain Models
Repository
5. Frontend Architecture
5.1 Framework
Angular (SPA)
TypeScript
RxJS for reactive flows


5.2 Route Structure
/login
/search
/routes
/notifications
/dispatcher/tasks
/admin/config
/admin/data
5.3 UI Composition
App shell (layout + navigation)
Role-based views
Smart search components
Task dashboard
Notification center
Admin configuration panels
5.4 Key UI Components
search bar with autocomplete
search results list with ranking labels
notification center panel
dispatcher task board
workflow progress tracker
admin rule editor
data ingestion dashboard


6. Application Services Layer (Backend)
6.1 AuthService

Responsibilities:

user authentication
password hashing (BCrypt)
session/token management
role validation
6.2 SearchService

Responsibilities:

keyword search
pinyin and initials matching
scoring and ranking
autocomplete suggestions
6.3 NotificationService

Responsibilities:

create notifications
apply user preferences
enforce DND rules
message center queries


6.4 QueueService

Responsibilities:

enqueue events
retry failed jobs
deduplicate messages
process background tasks
6.5 SchedulerService

Responsibilities:

run periodic jobs
trigger reminders
process overdue events
clean expired data
6.6 WorkflowService

Responsibilities:

manage task states
handle approvals (single/parallel)
branching logic
escalation handling
6.7 TaskService

Responsibilities:

create and assign tasks
batch operations
track progress
enforce SLA rules
6.8 DataIngestionService

Responsibilities:

parse HTML/JSON data
map fields
version datasets
store raw + parsed data
6.9 DataCleaningService

Responsibilities:

normalize fields
apply cleaning rules
handle missing values
log transformations

6.10 ConfigService

Responsibilities:

manage templates
manage sorting weights
manage dictionaries
dynamic configuration loading
6.11 ObservabilityService

Responsibilities:

structured logging
trace ID generation
metrics collection
health checks

7. Data Persistence Design
7.1 Database

Primary: PostgreSQL

7.2 Core Tables
users
roles
routes
stops
route_stop_map
search_index
notifications
tasks
workflow_states
configs
raw_data
parsed_data
data_versions
audit_logs
queue_messages


7.3 Persistence Principles
database is source of truth
all writes go through services
transactional consistency for critical operations
8. Domain Model Overview
8.1 User
id
username
password_hash
role
created_at
8.2 Route & Stop

Route:

id
name
frequency_score

Stop:

id
name
popularity_score

8.3 Notification
id
user_id
type
content
scheduled_time
status
8.4 Task
id
type
status
assigned_to
timeout_at
8.5 QueueMessage
id
type
payload
status
retry_count
next_retry_at
8.6 DataVersion
id
version
created_at


9. Search System Design
9.1 Search Strategy
PostgreSQL full-text search
precomputed pinyin fields
initials matching

9.2 Ranking Formula

score = (frequency_weight * route_frequency) + (popularity_weight * stop_popularity)



9.3 Autocomplete
debounce input (300ms)
limit results (top 10)
mixed results (routes + stops)
10. Notification System Design
10.1 Flow

Event → Queue → Consumer → Notification → UI


10.2 Features
arrival reminders (default 10 min)
missed alerts (5 min)
DND handling
retry logic
10.3 DND Logic
notifications delayed (not dropped)
rescheduled to valid time window
11. Workflow Engine Design
11.1 States
pending
approved
rejected
returned



11.2 Features
conditional branching
parallel approvals
escalation after 24h
task reassignment
11.3 Execution Model
state machine-based
transitions stored in DB
audit trail for all changes
12. Data Processing Design
12.1 Input
HTML
JSON

12.2 Pipeline

Raw Data → Parsing → Mapping → Cleaning → Storage → Versioning



12.3 Cleaning Rules
unit normalization (㎡, yuan/month)
NULL handling
configurable rules
audit logs
13. Queue & Retry Design
13.1 Queue Type
persistent DB-backed queue
13.2 Retry Policy
max 5 retries
exponential backoff
failure logging
13.3 Deduplication
idempotency key per event



14. Scheduler Design
14.1 Interval
runs every 60 seconds
14.2 Responsibilities
trigger reminders
process queue
detect SLA violations
clean expired tasks




14.3 Startup Recovery
process overdue jobs on restart
15. Authentication & Security Design
15.1 Auth Model
local username/password
no external identity provider
15.2 Password Handling
BCrypt hashing
salted passwords



15.3 Security Features
data desensitization
role-based access control
16. Observability Design
16.1 Logging
structured JSON logs
include trace IDs
16.2 Metrics
API latency
queue backlog
task processing time


16.3 Alerts
P95 > 500ms
queue backlog threshold
16.4 Health Checks
database connection
queue health
scheduler status
17. Error Handling Strategy
no silent failures
retry critical operations
log all errors with trace IDs
return safe responses to UI



18. Testing Strategy
Unit Tests
search logic
workflow transitions
queue retry logic
data cleaning
Integration Tests
API endpoints
database transactions
End-to-End Tests
search → notification → workflow
data ingestion → cleaning → storage
19. Deployment Design
Spring Boot JAR deployment
Angular static build
local LAN hosting
no internet dependency
20. System Hardening
20.1 Concurrency Control
DB transactions
optimistic locking where needed
20.2 Idempotency
all critical operations idempotent
20.3 Recovery
queue retry
scheduler catch-up



20.4 Data Integrity
strict validation before writes
21. Future Scalability

The system is designed to evolve into:

microservices (if needed)
external integrations
real-time messaging

Without major redesign due to:

modular architecture
clean service boundaries
repository abstraction





