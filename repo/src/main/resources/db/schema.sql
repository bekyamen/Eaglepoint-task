CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('PASSENGER', 'DISPATCHER', 'ADMIN')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE routes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL UNIQUE,
    frequency_score NUMERIC(10,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE stops (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL UNIQUE,
    popularity_score NUMERIC(10,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE route_stop_map (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    route_id UUID NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    stop_id UUID NOT NULL REFERENCES stops(id) ON DELETE CASCADE,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_route_stop_order UNIQUE (route_id, order_index),
    CONSTRAINT uq_route_stop UNIQUE (route_id, stop_id)
);

CREATE TABLE search_index (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_id UUID NOT NULL,
    entity_type VARCHAR(20) NOT NULL CHECK (entity_type IN ('ROUTE', 'STOP')),
    name VARCHAR(150) NOT NULL,
    name_tsv tsvector GENERATED ALWAYS AS (to_tsvector('simple', lower(name))) STORED,
    pinyin VARCHAR(255) NOT NULL,
    initials VARCHAR(64) NOT NULL,
    frequency_score NUMERIC(10,4),
    popularity_score NUMERIC(10,4),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_search_index_entity UNIQUE (entity_type, entity_id)
);

CREATE OR REPLACE FUNCTION build_initials(input_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    token TEXT;
    result TEXT := '';
BEGIN
    IF input_text IS NULL OR btrim(input_text) = '' THEN
        RETURN '';
    END IF;

    FOR token IN
        SELECT unnest(regexp_split_to_array(lower(input_text), '[^a-z0-9]+'))
    LOOP
        IF token <> '' THEN
            result := result || substring(token, 1, 1);
        END IF;
    END LOOP;

    RETURN result;
END;
$$;

CREATE OR REPLACE FUNCTION upsert_route_search_index()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO search_index (
        id, entity_id, entity_type, name, pinyin, initials, frequency_score, popularity_score, created_at, updated_at
    )
    VALUES (
        gen_random_uuid(),
        NEW.id,
        'ROUTE',
        NEW.name,
        lower(NEW.name),
        build_initials(NEW.name),
        NEW.frequency_score,
        NULL,
        NOW(),
        NOW()
    )
    ON CONFLICT (entity_type, entity_id)
    DO UPDATE SET
        name = EXCLUDED.name,
        pinyin = EXCLUDED.pinyin,
        initials = EXCLUDED.initials,
        frequency_score = EXCLUDED.frequency_score,
        popularity_score = NULL,
        updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION upsert_stop_search_index()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO search_index (
        id, entity_id, entity_type, name, pinyin, initials, frequency_score, popularity_score, created_at, updated_at
    )
    VALUES (
        gen_random_uuid(),
        NEW.id,
        'STOP',
        NEW.name,
        lower(NEW.name),
        build_initials(NEW.name),
        NULL,
        NEW.popularity_score,
        NOW(),
        NOW()
    )
    ON CONFLICT (entity_type, entity_id)
    DO UPDATE SET
        name = EXCLUDED.name,
        pinyin = EXCLUDED.pinyin,
        initials = EXCLUDED.initials,
        frequency_score = NULL,
        popularity_score = EXCLUDED.popularity_score,
        updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION delete_route_search_index()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM search_index WHERE entity_type = 'ROUTE' AND entity_id = OLD.id;
    RETURN OLD;
END;
$$;

CREATE OR REPLACE FUNCTION delete_stop_search_index()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM search_index WHERE entity_type = 'STOP' AND entity_id = OLD.id;
    RETURN OLD;
END;
$$;

CREATE TRIGGER trg_route_search_index_upsert
AFTER INSERT OR UPDATE OF name, frequency_score ON routes
FOR EACH ROW
EXECUTE FUNCTION upsert_route_search_index();

CREATE TRIGGER trg_route_search_index_delete
AFTER DELETE ON routes
FOR EACH ROW
EXECUTE FUNCTION delete_route_search_index();

CREATE TRIGGER trg_stop_search_index_upsert
AFTER INSERT OR UPDATE OF name, popularity_score ON stops
FOR EACH ROW
EXECUTE FUNCTION upsert_stop_search_index();

CREATE TRIGGER trg_stop_search_index_delete
AFTER DELETE ON stops
FOR EACH ROW
EXECUTE FUNCTION delete_stop_search_index();

CREATE TABLE notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    dnd_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    dnd_start TIME,
    dnd_end TIME,
    preferred_channel VARCHAR(30) NOT NULL DEFAULT 'IN_APP',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_notification_preferences_user UNIQUE (user_id)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    scheduled_time TIMESTAMPTZ,
    sent_at TIMESTAMPTZ,
    dedup_key VARCHAR(150) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_notifications_dedup UNIQUE (dedup_key)
);

CREATE TABLE workflow_states (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_name VARCHAR(100) NOT NULL,
    current_state VARCHAR(100) NOT NULL,
    allowed_transitions JSONB NOT NULL,
    approval_chain_level INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_workflow_state UNIQUE (workflow_name, current_state)
);

CREATE TABLE workflow_transitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_state_id UUID NOT NULL REFERENCES workflow_states(id) ON DELETE CASCADE,
    from_state VARCHAR(100) NOT NULL,
    to_state VARCHAR(100) NOT NULL,
    transition_action VARCHAR(100) NOT NULL,
    requires_approval BOOLEAN NOT NULL DEFAULT FALSE,
    approval_chain_level INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(60) NOT NULL,
    status VARCHAR(30) NOT NULL,
    assigned_to UUID REFERENCES users(id) ON DELETE SET NULL,
    workflow_state_id UUID REFERENCES workflow_states(id) ON DELETE SET NULL,
    timeout_at TIMESTAMPTZ,
    payload JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE task_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    action VARCHAR(100) NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30),
    actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE data_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_name VARCHAR(100) NOT NULL,
    version_label VARCHAR(80) NOT NULL,
    checksum VARCHAR(128),
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_data_version UNIQUE (source_name, version_label)
);

CREATE TABLE raw_data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    data_version_id UUID REFERENCES data_versions(id) ON DELETE SET NULL,
    source_name VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    ingest_status VARCHAR(30) NOT NULL,
    received_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE parsed_data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    raw_data_id UUID REFERENCES raw_data(id) ON DELETE SET NULL,
    data_version_id UUID REFERENCES data_versions(id) ON DELETE SET NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    parsed_payload JSONB NOT NULL,
    validation_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    module_name VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    entity_type VARCHAR(60),
    entity_id UUID,
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE queue_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(60) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(30) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 5,
    next_retry_at TIMESTAMPTZ,
    lock_expires_at TIMESTAMPTZ,
    available_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    locked_at TIMESTAMPTZ,
    idempotency_key VARCHAR(150) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_queue_messages_idempotency UNIQUE (idempotency_key)
);

CREATE TABLE scheduler_state (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheduler_key VARCHAR(100) NOT NULL UNIQUE,
    last_run_at TIMESTAMPTZ,
    next_run_at TIMESTAMPTZ,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(120) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_group VARCHAR(80),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_routes_name ON routes(name);
CREATE INDEX idx_stops_name ON stops(name);
CREATE INDEX idx_search_index_name_lower ON search_index(lower(name));
CREATE INDEX idx_search_index_name_tsv ON search_index USING GIN(name_tsv);
CREATE INDEX idx_search_index_pinyin ON search_index(pinyin);
CREATE INDEX idx_search_index_initials ON search_index(initials);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_queue_messages_status ON queue_messages(status);
CREATE INDEX idx_queue_messages_next_retry ON queue_messages(next_retry_at);
CREATE INDEX idx_notifications_status_scheduled_time ON notifications(status, scheduled_time);
