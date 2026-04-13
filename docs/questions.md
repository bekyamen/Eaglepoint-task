# questions.md

## 1. User Account Creation vs Predefined Accounts

**Question:** The system specifies local authentication but does not clarify whether passengers, dispatchers, and admins are pre-created or can self-register.

**Assumption:** 
- Passengers can self-register
- Dispatchers and Admins are created by Admin only

**Solution:**
- Implement public registration for passengers
- Admin panel for creating dispatcher/admin accounts
- Enforce role-based access control (RBAC)

---

## 2. Role Scope & Multi-Role Support

**Question:** Can a single user have multiple roles (e.g., admin + dispatcher)?

**Assumption:** Each user has exactly one role.

**Solution:**
- Store a single role per user
- Enforce strict role-based UI and API authorization

---

## 3. Search Matching Rules (Critical)

**Question:** The prompt mentions keyword, pinyin, and initial matching but does not define priority or fallback logic.

**Assumption:**
Search priority:
1. Exact match (route/stop name)
2. Pinyin full match
3. Initial-letter match
4. Fuzzy keyword match

**Solution:**
- Precompute searchable fields (pinyin + initials)
- Implement weighted scoring system

---

## 4. Search Ranking Formula

**Question:** “frequency priority + stop popularity” is mentioned but not defined mathematically.

**Assumption:**  “score = (frequency_weight * route_frequency) + (popularity_weight * stop_popularity)



**Solution:**
- Store weights in admin-configurable table
- Allow dynamic tuning without redeploy

---

## 5. Autocomplete Behavior

**Question:** Should autocomplete return routes, stops, or both?

**Assumption:** Mixed results with type labels (Route / Stop)

**Solution:**
- Return unified results with tags
- Limit to top N (e.g., 10 results)
- Debounce input (300ms)

---

## 6. Notification Delivery Timing

**Question:** The system defines reminders (10 min before, 5 min after) but not how delays or system downtime affect them.

**Assumption:** Missed notifications should still be delivered when system resumes.

**Solution:**
- On scheduler restart, process overdue notifications
- Mark notifications with status: pending / sent / expired

---

## 7. Do-Not-Disturb (DND) Logic

**Question:** If a notification falls within DND hours, should it be dropped or delayed?

**Assumption:** Notifications are delayed, not dropped.

**Solution:**
- Reschedule notifications to next allowed time window
- Store DND preferences per user

---

## 8. Notification Deduplication

**Question:** What prevents duplicate notifications for the same event?

**Assumption:** Each notification has a unique event key.

**Solution:**
- Use idempotency key per event
- Prevent duplicate inserts in queue

---

## 9. Message Queue Implementation

**Question:** The prompt requires a queue but does not define persistence or failure recovery.

**Assumption:** Queue must be persistent and recoverable.

**Solution:**
- Implement DB-backed queue
- Retry failed jobs with exponential backoff
- Track retry count and status

---

## 10. Workflow Engine Complexity

**Question:** The workflow supports branching, parallel approvals, and returns but does not define limits.

**Assumption:**
- Max 3 approval levels
- Parallel approvals require all approvals to proceed

**Solution:**
- Implement configurable workflow definitions
- Store state transitions in DB

---

## 11. Task Timeout Handling

**Question:** Tasks escalate after 24 hours, but escalation behavior is unclear.

**Assumption:** Escalation sends alert and reassigns task.

**Solution:**
- Add escalation flag + timestamp
- Notify admin/next-level dispatcher
- Track SLA breaches

---

## 12. Batch Processing Scope

**Question:** Batch processing is mentioned but not defined.

**Assumption:** Dispatchers can approve/reject multiple tasks at once.

**Solution:**
- Implement bulk actions (approve/reject)
- Ensure transactional consistency

---

## 13. Data Parsing Error Handling

**Question:** What happens if HTML/JSON parsing fails?

**Assumption:** Invalid records should not break pipeline.

**Solution:**
- Log errors with raw data snapshot
- Skip invalid entries
- Store failure logs for admin review

---

## 14. Data Versioning Strategy

**Question:** The system tracks version changes but not granularity.

**Assumption:** Versioning occurs per dataset import.

**Solution:**
- Store version_id for each import batch
- Maintain history of changes
- Allow rollback if needed

---

## 15. Data Cleaning Rule Ownership

**Question:** Who defines cleaning rules and when are they applied?

**Assumption:** Admin defines rules and they apply during ingestion.

**Solution:**
- Admin UI for rule management
- Apply rules in parsing pipeline
- Log before/after values

---

## 16. Missing Data Handling

**Question:** Missing values are marked NULL, but how are they displayed?

**Assumption:** UI should handle NULL gracefully.

**Solution:**
- Display placeholders (e.g., “N/A”)
- Log missing fields with source reference

---

## 17. Observability Scope

**Question:** Which operations require trace IDs?

**Assumption:** Only critical workflows need tracing.

**Solution:**
- Add trace IDs for:
  - search requests
  - workflow actions
  - queue processing
  - data parsing

---

## 18. Performance Constraints

**Question:** The system defines P95 > 500ms alert but not expected load.

**Assumption:** Moderate city-scale usage.

**Solution:**
- Optimize search queries with indexing
- Cache frequent queries if needed

---

## 19. API Failure Handling

**Question:** What happens if backend APIs fail?

**Assumption:** System should degrade gracefully.

**Solution:**
- Return fallback responses
- Log failures
- Retry critical operations

---

## 20. Data Sync Frequency

**Question:** How often is bus data updated?

**Assumption:** Daily or manual import.

**Solution:**
- Support manual trigger + scheduled ingestion
- Track last update timestamp

---

## 21. Stop Popularity Calculation

**Question:** Popularity metric is undefined.

**Assumption:** Based on search frequency.

**Solution:**
- Increment counter on search/select
- Periodically normalize scores

---

## 22. Security Scope (Desensitization)

**Question:** What data is considered sensitive?

**Assumption:** User info and operational logs.

**Solution:**
- Mask sensitive fields in logs
- Apply role-based visibility

---

## 23. Local Deployment Constraints

**Question:** System runs in LAN but does not define multi-user concurrency.

**Assumption:** Multiple users access same backend server.

**Solution:**
- Ensure DB transaction safety
- Handle concurrent updates properly

---

## 24. Backup & Recovery Strategy

**Question:** Backup is mentioned but restore process is not defined.

**Assumption:** Full database backup and manual restore.

**Solution:**
- Use scheduled pg_dump
- Provide admin restore procedure

---

## 25. UI Data Freshness

**Question:** How often should frontend refresh data?

**Assumption:** Near real-time for critical data.

**Solution:**
- Polling or manual refresh
- Avoid WebSockets (offline constraint)
