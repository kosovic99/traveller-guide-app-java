-- Non-destructive deprecation marker for legacy backup tables.
-- This does not drop or modify the backup tables.

CREATE TABLE IF NOT EXISTS schema_deprecations (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  object_name varchar(128) NOT NULL,
  object_type varchar(32) NOT NULL,
  reason varchar(255) NOT NULL,
  planned_action varchar(255) NOT NULL,
  created_at timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (id),
  UNIQUE KEY uq_schema_deprecations_object (object_name, object_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO schema_deprecations (
  object_name,
  object_type,
  reason,
  planned_action
)
VALUES
  ('countries_bak', 'table', 'Legacy backup table should not be part of runtime schema', 'Export and verify external backup before removal'),
  ('cities_bak', 'table', 'Legacy backup table should not be part of runtime schema', 'Export and verify external backup before removal'),
  ('attractions_bak', 'table', 'Legacy backup table should not be part of runtime schema', 'Export and verify external backup before removal')
ON DUPLICATE KEY UPDATE
  reason = VALUES(reason),
  planned_action = VALUES(planned_action);
