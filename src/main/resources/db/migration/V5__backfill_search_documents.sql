-- Non-destructive backfill for the derived search read model.
-- Uses upsert semantics so local/manual reruns update derived rows rather than duplicating them.

INSERT INTO search_documents (
  entity_type,
  entity_id,
  title,
  subtitle,
  country_slug,
  city_slug,
  attraction_slug,
  rank_score,
  is_public
)
SELECT
  'country',
  c.id,
  c.name,
  NULL,
  c.slug,
  NULL,
  NULL,
  0,
  1
FROM countries c
WHERE c.slug IS NOT NULL
  AND c.slug <> ''
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  subtitle = VALUES(subtitle),
  country_slug = VALUES(country_slug),
  city_slug = VALUES(city_slug),
  attraction_slug = VALUES(attraction_slug),
  rank_score = VALUES(rank_score),
  is_public = VALUES(is_public);

INSERT INTO search_documents (
  entity_type,
  entity_id,
  title,
  subtitle,
  country_slug,
  city_slug,
  attraction_slug,
  rank_score,
  is_public
)
SELECT
  'city',
  ci.id,
  ci.name,
  co.name,
  co.slug,
  ci.slug,
  NULL,
  COALESCE(ci.popularity_score, 0),
  1
FROM cities ci
JOIN countries co ON co.id = ci.country_id
WHERE ci.slug IS NOT NULL
  AND ci.slug <> ''
  AND co.slug IS NOT NULL
  AND co.slug <> ''
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  subtitle = VALUES(subtitle),
  country_slug = VALUES(country_slug),
  city_slug = VALUES(city_slug),
  attraction_slug = VALUES(attraction_slug),
  rank_score = VALUES(rank_score),
  is_public = VALUES(is_public);

INSERT INTO search_documents (
  entity_type,
  entity_id,
  title,
  subtitle,
  country_slug,
  city_slug,
  attraction_slug,
  rank_score,
  is_public
)
SELECT
  'attraction',
  a.id,
  a.name,
  ci.name,
  co.slug,
  ci.slug,
  a.slug,
  0,
  1
FROM attractions a
JOIN cities ci ON ci.id = a.city_id
JOIN countries co ON co.id = ci.country_id
WHERE a.slug IS NOT NULL
  AND a.slug <> ''
  AND ci.slug IS NOT NULL
  AND ci.slug <> ''
  AND co.slug IS NOT NULL
  AND co.slug <> ''
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  subtitle = VALUES(subtitle),
  country_slug = VALUES(country_slug),
  city_slug = VALUES(city_slug),
  attraction_slug = VALUES(attraction_slug),
  rank_score = VALUES(rank_score),
  is_public = VALUES(is_public);
