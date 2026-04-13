1. User Account Model

Question: Are users pre-created or can they self-register?

Assumption:

Passengers can self-register
Dispatchers/Admins are created by Admin

Solution:

Public registration endpoint for passengers
Admin panel for privileged roles
Enforce RBAC across API + UI
2. Role Model & Authorization

Question: Can a user have multiple roles?

Assumption: One user = one role

Solution:

Store a single role per user
Enforce strict role-based access control
Middleware-based authorization checks
3. Search Matching Logic (Core Feature)

Question: How should keyword, pinyin, and initials be prioritized?

Assumption (priority order):

Exact match
Full pinyin match
Initial letters
Fuzzy keyword

Solution:

Precompute searchable fields
Implement weighted scoring
4. Search Ranking Formula

Question: How is ranking calculated?

Assumption:
score = frequency_weight × route_frequency + popularity_weight × stop_popularity

Solution:

Store weights in config (admin adjustable)
Allow dynamic tuning
5. Autocomplete Behavior

Question: Should autocomplete return routes, stops, or both?

Assumption: Mixed results

Solution:

Unified response with type labels
Limit results (e.g., top 10)
Add debounce (~300ms)
6. Notification Reliability

Question: What happens if notifications are missed due to downtime?

Assumption: Missed notifications must still be delivered

Solution:

Persistent queue
On restart, process pending notifications
Track status: pending / sent / expired
7. Do-Not-Disturb (DND) Handling

Question: Should notifications be dropped or delayed?

Assumption: Delayed

Solution:

Reschedule to next valid time window
Store user DND preferences
8. Message Queue Design

Question: How is reliability ensured?

Assumption: Persistent queue required

Solution:

DB-backed queue
Retry with exponential backoff
Track retry count + failure status
9. Workflow Engine Scope


Question: What are the limits of workflow complexity?

Assumption:

Max 3 approval levels
Parallel approvals require all approvals

Solution:

Configurable workflow definitions
Persist state transitions

10. Data Ingestion & Error Handling

Question: What happens when parsing fails?

Assumption: Failures should not break pipeline

Solution:

Skip invalid records
Log raw data + errors
Provide admin review logs

