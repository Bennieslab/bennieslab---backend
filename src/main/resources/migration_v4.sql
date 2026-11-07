-- =============================================================
-- Bennieslab v4 — Project Live URL & App Releases Table
-- Target Database: PostgreSQL (Aiven / Render)
-- Run this on your database console / query editor.
-- =============================================================

-- 1. Add 'live_url' to 'project' table (Task A)
ALTER TABLE project
    ADD COLUMN IF NOT EXISTS live_url VARCHAR(2048) DEFAULT NULL;

-- 2. Create 'app_release' table (Task C)
CREATE TABLE IF NOT EXISTS app_release (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255)  NOT NULL,
    version       VARCHAR(50)   NOT NULL,
    category      VARCHAR(100)  DEFAULT NULL,
    status        VARCHAR(50)   DEFAULT 'Stable',
    description   TEXT          DEFAULT NULL,
    download_url  VARCHAR(2048) DEFAULT NULL,
    github_url    VARCHAR(2048) DEFAULT NULL,
    thumbnail_url VARCHAR(2048) DEFAULT NULL,
    platforms     VARCHAR(255)  DEFAULT NULL,
    pinned        BOOLEAN       NOT NULL DEFAULT FALSE,
    sort_order    INTEGER       NOT NULL DEFAULT 0,
    date_posted   TIMESTAMP     DEFAULT NULL,
    last_updated  TIMESTAMP     DEFAULT NULL
);

-- 3. Verification Query
SELECT table_name, column_name, data_type, is_nullable
FROM   information_schema.columns
WHERE  table_schema = 'public'
  AND  (
       (table_name = 'project' AND column_name = 'live_url')
    OR (table_name = 'app_release')
  )
ORDER  BY table_name, ordinal_position;
