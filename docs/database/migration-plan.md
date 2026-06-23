# Safe Database Migration Plan

This plan documents non-destructive Flyway migrations for the imported World4You MariaDB/MySQL schema.

## Rules

- Do not run these migrations against production without manual review.
- Do not delete tables or columns in the first migration phase.
- Preserve all existing data.
- Treat `*_bak` tables as deprecated only after external backup verification.
- Keep Flyway disabled until the baseline and migration order are approved.

## Proposed Migration Files

| File | Purpose | Risk |
|---|---|---|
| `V1__baseline_existing_world4you_schema.sql` | Baseline marker for existing schema | Low |
| `V2__add_quality_score_columns.sql` | Add structured ranking fields to `cities` | Low-Medium |
| `V3__add_safe_read_indexes.sql` | Add read/query optimization indexes | Low-Medium |
| `V4__add_search_documents_table.sql` | Add derived search read model | Low-Medium |
| `V5__backfill_search_documents.sql` | Populate search read model from source tables | Low |
| `V6__deprecate_legacy_backup_tables.sql` | Mark backup tables as deprecated, without deleting them | Low |

## Rollback Strategy

Flyway Community does not execute automatic undo migrations. Rollback must be a manual operational procedure:

- Take a full database backup before applying any production migration.
- Roll back additive columns by dropping only newly added columns.
- Roll back new indexes by dropping only newly added indexes.
- Roll back `search_documents` by dropping that derived table or clearing it.
- Roll back deprecation metadata by deleting rows from `schema_deprecations`.
- Never drop original data tables as part of emergency rollback.

## Local Development Only

The following are safe only for local testing unless separately approved:

- Rebuilding `search_documents`.
- Testing index creation and removal.
- Testing scoring/backfill rules.
- Testing future renames such as `foto` to `photo_url`.
- Exporting and then removing `*_bak` tables from a local database copy.

## Requires Manual Production Review

- Enabling Flyway on an existing production database.
- Setting `baseline-on-migrate=true`.
- Adding columns to large production tables.
- Creating FULLTEXT indexes.
- Backfilling derived read models.
- Changing `slug` columns to `NOT NULL`.
- Dropping `uq_cities_name`.
- Dropping `*_bak` tables.
- Renaming `foto`, `flag`, or any public API-backed column.

## Deferred Destructive Changes

Do not drop backup tables in the first migration phase:

```sql
DROP TABLE countries_bak;
DROP TABLE cities_bak;
DROP TABLE attractions_bak;
```

Use a deprecation phase first:

1. Export backup tables externally.
2. Verify row counts and restore capability.
3. Confirm the API never queries these tables.
4. Mark them deprecated.
5. Remove them only in a later approved migration.

Do not drop `uq_cities_name` yet. First verify duplicate city names and confirm `(country_id, slug)` is the canonical city identity.

Do not rename `foto` yet. First add a new column, backfill, update Java, keep both fields for one release, then remove the legacy column later.
