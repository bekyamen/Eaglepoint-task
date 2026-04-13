# Delivery Acceptance and Project Architecture Audit (Static-Only, Retest)

## 1. Verdict
- **Overall conclusion: Partial Pass**

## 2. Scope and Static Verification Boundary
- **Reviewed:** `repo/` only (README/config/manifests, backend controllers/services/security/entities/repositories, Angular routing/pages/services, static tests).
- **Not reviewed:** Runtime behavior, deployment/network behavior, UI rendering, scheduler timing, database migration execution outcomes.
- **Intentionally not executed:** project startup, Docker, test commands, external services.
- **Manual verification required:** true runtime flow integrity, scheduler throughput under load, observability alert effectiveness, and end-to-end UX correctness.

## 3. Repository / Requirement Mapping Summary
- Prompt expects offline LAN full-stack platform with passenger search + notifications prefs, dispatcher workflow state machine, admin config/templates/dictionaries/cleaning/ingestion, secure auth, queue scheduler notifications, and observability.
- Implementation in `repo/` includes substantial REST surface (auth/search/routes/stops/tasks/workflow/notifications/admin), Postgres schema breadth, queue scheduler services, and some backend/frontend tests.
- Major residual gap: several prompt-critical capabilities are delivered only as read/list surfaces or placeholders, not full management workflows.


## 4. Section-by-section Review

### 1. Hard Gates


#### 1.1 Documentation and static verifiability
- **Conclusion: Pass**
- **Rationale:** README includes run/test/config instructions for Docker and non-Docker usage with concrete commands and endpoint mapping.
- **Evidence:** `repo/README.md:13`, `repo/README.md:22`, `repo/README.md:76`, `repo/README.md:68`
- **Manual verification note:** runtime startup success still requires manual execution.


#### 1.2 Material deviation from Prompt
- **Conclusion: Partial Pass**
- **Rationale:** architecture and major modules align, but key prompt behaviors (state-machine actions, preference management APIs, ingestion/cleaning pipelines, observability depth) are only partially implemented.
- **Evidence:** `repo/src/main/java/com/citybus/platform/modules/workflow/controller/WorkflowController.java:21`, `repo/src/main/java/com/citybus/platform/modules/task/controller/TaskController.java:24`, `repo/src/main/java/com/citybus/platform/modules/data/controller/DataController.java:21`, `repo/src/main/java/com/citybus/platform/modules/system/controller/SystemController.java:20`

### 2. Delivery Completeness



#### 2.1 Core explicit requirements coverage
- **Conclusion: Partial Pass**
- **Rationale:** core APIs exist for auth/search/routes/stops/tasks/workflow/list notifications/admin lists; but required “management” semantics (workflow approvals/branching/escalation actions, notification preference APIs, ingestion parsing/cleaning operations) are not statically evidenced.
- **Evidence:** `repo/src/main/java/com/citybus/platform/modules/notification/controller/NotificationController.java:27`, `repo/src/main/java/com/citybus/platform/modules/workflow/controller/WorkflowController.java:23`, `repo/src/main/java/com/citybus/platform/modules/data/service/DataServiceImpl.java:19`, `repo/src/main/java/com/citybus/platform/modules/notification/service/NotificationServiceImpl.java:74`
- **Manual verification note:** none; missing action APIs are visible statically.


#### 2.2 End-to-end deliverable completeness (0->1)
- **Conclusion: Partial Pass**
- **Rationale:** deliverable is now coherent and cross-layer for many list/detail flows; still incomplete for several prompt-critical operational flows.
- **Evidence:** `repo/src/app/core/services/admin-ops.service.ts:32`, `repo/src/main/java/com/citybus/platform/modules/config/controller/ConfigController.java:30`, `repo/src/main/java/com/citybus/platform/modules/data/controller/DataController.java:30`, `repo/src/app/features/passenger/preferences-page.component.ts:7`

### 3. Engineering and Architecture Quality

#### 3.1 Structure and module decomposition
- **Conclusion: Pass**
- **Rationale:** clear modular monolith boundaries with dedicated controller/service/repository/entity layers and wide schema support.
- **Evidence:** `repo/src/main/java/com/citybus/platform/modules/task/controller/TaskController.java:17`, `repo/src/main/java/com/citybus/platform/modules/system/service/SchedulerServiceImpl.java:22`, `repo/src/main/resources/db/schema.sql:219`

#### 3.2 Maintainability and extensibility
- **Conclusion: Partial Pass**
- **Rationale:** layering is maintainable, but some requirement-critical behavior is hard-coded or read-only (e.g., ranking weight constant, no config mutation APIs).
- **Evidence:** `repo/src/main/java/com/citybus/platform/modules/search/service/SearchServiceImpl.java:21`, `repo/src/main/java/com/citybus/platform/modules/config/controller/ConfigController.java:21`

### 4. Engineering Details and Professionalism

#### 4.1 Error handling, logging, validation, API design
- **Conclusion: Partial Pass**
- **Rationale:** validation and centralized exception handling exist; logging/metrics/tracing are not at prompt-required depth.
- **Evidence:** `repo/src/main/java/com/citybus/platform/common/error/ApiExceptionHandler.java:13`, `repo/src/main/java/com/citybus/platform/modules/auth/controller/AuthController.java:24`, `repo/src/main/java/com/citybus/platform/modules/system/service/SchedulerServiceImpl.java:58`, `repo/src/main/resources/application-docker.yml:25`

#### 4.2 Product-grade vs demo shape
- **Conclusion: Partial Pass**
- **Rationale:** product-like scaffolding exists; however several pages/flows remain simplistic and not full operational tooling (notably preferences and workflow actioning).
- **Evidence:** `repo/src/app/features/passenger/preferences-page.component.ts:17`, `repo/src/main/java/com/citybus/platform/modules/workflow/controller/WorkflowController.java:21`, `repo/src/main/java/com/citybus/platform/modules/task/controller/TaskController.java:24`

### 5. Prompt Understanding and Requirement Fit

#### 5.1 Business objective and constraint fit
- **Conclusion: Partial Pass**
- **Rationale:** prompt intent is mostly understood (roles, offline stack, queue, search with pinyin support), but high-value business semantics are incomplete (full dispatcher workflow engine actions, ingestion+cleaning operations, performance alerting).
- **Evidence:** `repo/src/main/java/com/citybus/platform/modules/search/service/SearchTextNormalizer.java:25`, `repo/src/main/java/com/citybus/platform/modules/system/service/SchedulerServiceImpl.java:43`, `repo/src/main/java/com/citybus/platform/modules/data/service/DataServiceImpl.java:19`

### 6. Aesthetics (frontend)

#### 6.1 Visual and interaction quality
- **Conclusion: Partial Pass**
- **Rationale:** basic UI hierarchy and role-separated modules are present, but interaction richness and management controls are limited in key pages.
- **Evidence:** `repo/src/app/app-routing.module.ts:14`, `repo/src/app/features/admin/audit-page.component.ts:7`, `repo/src/app/features/passenger/preferences-page.component.ts:7`
- **Manual verification note:** final visual consistency requires manual browser review.

## 5. Issues / Suggestions (Severity-Rated)

### Blocker
1. **Severity:** Blocker  
   **Title:** Dispatcher workflow engine lacks action endpoints (approval/branch/escalation/batch)  
   **Conclusion:** Fail  
   **Evidence:** `repo/src/main/java/com/citybus/platform/modules/workflow/controller/WorkflowController.java:21`, `repo/src/main/java/com/citybus/platform/modules/task/controller/TaskController.java:24`, `repo/src/main/java/com/citybus/platform/modules/workflow/repository/WorkflowRepository.java:7`  
   **Impact:** Prompt’s core dispatcher operations cannot be completed via delivered APIs.  
   **Minimum actionable fix:** Add workflow/task command endpoints and service logic for approve/reject/branch/escalate/batch transitions with audit trail.

### High
2. **Severity:** High  
   **Title:** Passenger notification preferences are UI-only, no backend API persistence  
   **Conclusion:** Fail  
   **Evidence:** `repo/src/app/features/passenger/preferences-page.component.ts:17`, `repo/src/main/java/com/citybus/platform/modules/notification/controller/NotificationController.java:27`, `repo/src/main/java/com/citybus/platform/modules/notification/service/NotificationServiceImpl.java:74`  
   **Impact:** quiet hours/reminder preferences are not user-manageable end-to-end from API contract.  
   **Minimum actionable fix:** add GET/PUT preferences endpoints and persist through `NotificationPreferenceEntity`.

3. **Severity:** High  
   **Title:** Ingestion/cleaning pipeline required by prompt is not implemented beyond version listing  
   **Conclusion:** Fail  
   **Evidence:** `repo/src/main/java/com/citybus/platform/modules/data/controller/DataController.java:21`, `repo/src/main/java/com/citybus/platform/modules/data/service/DataServiceImpl.java:19`, `repo/src/main/java/com/citybus/platform/modules/data/repository/DataRepository.java:7`  
   **Impact:** HTML/JSON parsing, mapping, cleaning-rule execution, and traceable null normalization are not evidenced.  
   **Minimum actionable fix:** implement ingestion endpoints/services for parse-map-clean-version flows with audit logging.

4. **Severity:** High  
   **Title:** Search ranking weight is hard-coded, not admin-manageable as required  
   **Conclusion:** Fail  
   **Evidence:** `repo/src/main/java/com/citybus/platform/modules/search/service/SearchServiceImpl.java:21`, `repo/src/main/java/com/citybus/platform/modules/config/controller/ConfigController.java:21`  
   **Impact:** admin cannot tune ranking behavior despite prompt requirement.  
   **Minimum actionable fix:** load ranking weights from config storage and provide admin mutation APIs with validation.

5. **Severity:** High  
   **Title:** Security test coverage is weak; controller tests disable filters  
   **Conclusion:** Partial Fail  
   **Evidence:** `repo/src/test/java/com/citybus/platform/modules/auth/controller/AuthControllerTest.java:21`, `repo/src/test/java/com/citybus/platform/modules/task/controller/TaskControllerTest.java:20`, `repo/src/test/java/com/citybus/platform/modules/config/controller/ConfigControllerTest.java:19`  
   **Impact:** 401/403/role-boundary regressions can remain undetected while tests still pass.  
   **Minimum actionable fix:** add security-enabled integration tests for unauthenticated/unauthorized paths and admin protections.

6. **Severity:** High  
   **Title:** Observability requirements under-delivered (metrics/P95/backlog alerts/trace propagation)  
   **Conclusion:** Partial Fail  
   **Evidence:** `repo/src/main/resources/application-docker.yml:29`, `repo/src/main/java/com/citybus/platform/modules/system/controller/SystemController.java:20`, `repo/src/main/java/com/citybus/platform/common/error/ApiExceptionHandler.java:52`  
   **Impact:** prompt-level monitoring commitments are not statically demonstrated.  
   **Minimum actionable fix:** expose metrics endpoints, add timers/histograms for API P95, queue backlog alert thresholds, and generated trace-id propagation.

### Medium
7. **Severity:** Medium  
   **Title:** `ingestions` endpoint returns version list, not ingestion records  
   **Conclusion:** Fail  
   **Evidence:** `repo/src/main/java/com/citybus/platform/modules/data/controller/DataController.java:30`, `repo/src/main/java/com/citybus/platform/modules/data/controller/DataController.java:35`  
   **Impact:** API semantics mismatch can mislead admin UI and reviewers.  
   **Minimum actionable fix:** return ingestion DTOs backed by raw/parsed ingestion repositories.

## 6. Security Review Summary
- **Authentication entry points:** **Pass**; local register/login + JWT + BCrypt are present.  
  Evidence: `repo/src/main/java/com/citybus/platform/modules/auth/controller/AuthController.java:23`, `repo/src/main/java/com/citybus/platform/config/security/SecurityConfig.java:67`, `repo/src/main/java/com/citybus/platform/modules/auth/security/JwtFilter.java:27`
- **Route-level authorization:** **Pass**; URL + method role checks are present.  
  Evidence: `repo/src/main/java/com/citybus/platform/config/security/SecurityConfig.java:38`, `repo/src/main/java/com/citybus/platform/modules/config/controller/ConfigController.java:22`
- **Object-level authorization:** **Partial Pass**; explicit ownership check exists for notification read, but broader object-level checks are limited by missing action APIs.  
  Evidence: `repo/src/main/java/com/citybus/platform/modules/notification/service/NotificationServiceImpl.java:104`
- **Function-level authorization:** **Partial Pass**; protected annotations are used, but security test verification is shallow.  
  Evidence: `repo/src/main/java/com/citybus/platform/modules/task/controller/TaskController.java:25`, `repo/src/test/java/com/citybus/platform/modules/task/controller/TaskControllerTest.java:20`
- **Tenant/user isolation:** **Cannot Confirm Statistically**; user-scoped notification access exists, but no explicit tenant model and limited cross-resource isolation tests.  
  Evidence: `repo/src/main/java/com/citybus/platform/modules/notification/repository/NotificationRepository.java:18`
- **Admin/internal/debug protection:** **Partial Pass**; admin endpoints are role-protected, but test coverage does not assert enforced behavior with active filters.  
  Evidence: `repo/src/main/java/com/citybus/platform/modules/system/controller/SystemController.java:21`, `repo/src/test/java/com/citybus/platform/modules/config/controller/ConfigControllerTest.java:19`

## 7. Tests and Logging Review
- **Unit tests:** **Partial Pass**; present for service logic and utility normalization.  
  Evidence: `repo/src/test/java/com/citybus/platform/modules/notification/service/NotificationServiceImplTest.java:24`, `repo/src/test/java/com/citybus/platform/modules/search/service/SearchTextNormalizerTest.java:8`
- **API/integration tests:** **Partial Pass**; controller tests exist but are web-layer with mocked services and disabled security filters.  
  Evidence: `repo/src/test/java/com/citybus/platform/modules/auth/controller/AuthControllerTest.java:20`, `repo/src/test/java/com/citybus/platform/modules/task/controller/TaskControllerTest.java:19`
- **Logging categories/observability:** **Partial Pass**; limited structured logging; no static evidence of full metrics/trace monitoring setup.  
  Evidence: `repo/src/main/java/com/citybus/platform/modules/system/service/SchedulerServiceImpl.java:58`, `repo/src/main/resources/application-docker.yml:29`
- **Sensitive-data leakage risk in logs/responses:** **Partial Pass**; no clear password logging, but trace-id is request-header passthrough (not controlled generation).  
  Evidence: `repo/src/main/java/com/citybus/platform/common/error/ApiExceptionHandler.java:52`

## 8. Test Coverage Assessment (Static Audit)

### 8.1 Test Overview
- Unit and API-layer tests exist.
- Frameworks: JUnit/Mockito/Spring MockMvc (`spring-boot-starter-test`) and Angular spec tests (`ng test`).
- Test entry points: `repo/src/test/java/...` and `repo/src/app/**/*.spec.ts`.
- Documentation includes test commands.
- Evidence: `repo/pom.xml:77`, `repo/package.json:8`, `repo/README.md:22`, `repo/src/test/java/com/citybus/platform/modules/auth/controller/AuthControllerTest.java:20`, `repo/src/app/core/services/task.service.spec.ts:5`.

### 8.2 Coverage Mapping Table
| Requirement / Risk Point | Mapped Test Case(s) | Key Assertion / Fixture / Mock | Coverage Assessment | Gap | Minimum Test Addition |
|---|---|---|---|---|---|
| Auth validation (bad payload) | `repo/src/test/java/com/citybus/platform/modules/auth/controller/AuthControllerTest.java:34` | asserts 400 on invalid register | basically covered | no security filter/token-path coverage | add login failure + 401/403 tests with filters enabled |
| Task endpoint payload shape | `repo/src/test/java/com/citybus/platform/modules/task/controller/TaskControllerTest.java:30` | asserts `$.success` and task fields | basically covered | authorization and object-scope untested | add authenticated role-matrix tests |
| Config templates/dictionaries list | `repo/src/test/java/com/citybus/platform/modules/config/controller/ConfigControllerTest.java:29` | asserts response keys/groups | basically covered | admin-only enforcement untested | add tests with non-admin token expecting 403 |
| Notification object ownership | `repo/src/test/java/com/citybus/platform/modules/notification/service/NotificationServiceImplTest.java:40` | expects 403 when non-owner marks read | sufficient (service-level) | no controller/security integration coverage | add API tests for owner vs non-owner behavior |
| Search pinyin normalization | `repo/src/test/java/com/citybus/platform/modules/search/service/SearchTextNormalizerTest.java:11` | asserts Chinese input maps to pinyin tokens | basically covered | repository ranking and weight config not tested | add search service/repository ranking tests |
| Frontend task service request | `repo/src/app/core/services/task.service.spec.ts:21` | asserts GET URL with status query | basically covered | role guard and auth lifecycle barely tested | add guards/auth interceptor behavior tests |
| Workflow state-machine actions | None | None | missing | no tests for approve/reject/branch/escalate/batch | add workflow command tests and transition validation |
| Ingestion/cleaning/null traceability | None | None | missing | core prompt functionality untested/unimplemented | add parser-cleaning tests plus controller tests |

### 8.3 Security Coverage Audit
- **authentication:** partially covered (validation only), not filter-chain enforced.
- **route authorization:** insufficient (tests disable filters).
- **object-level authorization:** partially covered for notification service only.
- **tenant/data isolation:** cannot confirm (no explicit tenant isolation tests).
- **admin/internal protection:** insufficient (no security-enabled tests on admin endpoints).

### 8.4 Final Coverage Judgment
- **Partial Pass**
- Major risks covered: basic payload validation and selected service logic.
- Major risks uncovered: real authz enforcement, workflow action integrity, ingestion/cleaning correctness, and observability/performance alert behavior.

## 9. Final Notes
- This retest corrects the audit scope to `repo/` within the current working directory.
- Conclusions are static-only and evidence-based; no runtime success is inferred.
- Root-cause gaps are primarily capability depth (management/action flows and security/observability verification), not repository structure.
