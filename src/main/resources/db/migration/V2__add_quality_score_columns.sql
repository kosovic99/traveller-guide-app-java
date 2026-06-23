-- Non-destructive additive migration.
-- Adds structured fields for future ranking while preserving the legacy `rating` column.

ALTER TABLE cities
  ADD COLUMN rating_label varchar(50) NULL AFTER rating,
  ADD COLUMN rating_score decimal(4,2) NULL AFTER rating_label,
  ADD COLUMN popularity_score int unsigned NOT NULL DEFAULT 0 AFTER rating_score,
  ADD COLUMN is_featured tinyint(1) NOT NULL DEFAULT 0 AFTER popularity_score;
