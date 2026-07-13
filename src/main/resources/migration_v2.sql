-- =============================================================
-- Bennieslab v2 — Pinning & Smart Sorting Schema Migration
-- Run this BEFORE deploying the updated backend JAR to Render.
-- =============================================================

-- Projects
ALTER TABLE project
    ADD COLUMN IF NOT EXISTS pinned     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

-- Blog Posts
ALTER TABLE blog_post
    ADD COLUMN IF NOT EXISTS pinned     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

-- Skills
ALTER TABLE skill
    ADD COLUMN IF NOT EXISTS pinned     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

-- Optional: verify columns were added
SELECT column_name, data_type, column_default, is_nullable
FROM   information_schema.columns
WHERE  table_name IN ('project', 'blog_post', 'skill')
  AND  column_name IN ('pinned', 'sort_order')
ORDER  BY table_name, column_name;
