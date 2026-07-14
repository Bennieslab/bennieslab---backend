-- =============================================================
-- Bennieslab v3 — Project GitHub URL
-- Run this before deploying the updated backend JAR if Hibernate
-- is not managing schema updates automatically.
-- =============================================================

ALTER TABLE project
    ADD COLUMN IF NOT EXISTS github_url VARCHAR(2048);

SELECT column_name, data_type, character_maximum_length, is_nullable
FROM   information_schema.columns
WHERE  table_name = 'project'
  AND  column_name = 'github_url';
