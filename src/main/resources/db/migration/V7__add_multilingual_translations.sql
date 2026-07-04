-- Adds multilingual read data without changing existing canonical tables.
-- Existing country/city/attraction/category columns remain the English fallback source.

CREATE TABLE country_translations (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  country_id bigint unsigned NOT NULL,
  locale varchar(8) NOT NULL,
  name varchar(150) NOT NULL,
  slug varchar(140) NOT NULL,
  description text NULL,
  created_at timestamp NULL DEFAULT current_timestamp(),
  updated_at timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (id),
  CONSTRAINT fk_country_trans_country FOREIGN KEY (country_id) REFERENCES countries (id),
  UNIQUE KEY uq_country_trans_country_locale (country_id, locale),
  UNIQUE KEY uq_country_trans_locale_slug (locale, slug),
  KEY idx_country_trans_locale_slug (locale, slug),
  KEY idx_country_trans_country_locale (country_id, locale)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE city_translations (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  city_id bigint unsigned NOT NULL,
  locale varchar(8) NOT NULL,
  name varchar(180) NOT NULL,
  slug varchar(140) NOT NULL,
  description longtext NULL,
  created_at timestamp NULL DEFAULT current_timestamp(),
  updated_at timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (id),
  CONSTRAINT fk_city_trans_city FOREIGN KEY (city_id) REFERENCES cities (id),
  UNIQUE KEY uq_city_trans_city_locale (city_id, locale),
  UNIQUE KEY uq_city_trans_locale_slug (locale, slug),
  KEY idx_city_trans_locale_slug (locale, slug),
  KEY idx_city_trans_city_locale (city_id, locale)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE attraction_translations (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  attraction_id bigint unsigned NOT NULL,
  locale varchar(8) NOT NULL,
  name varchar(255) NOT NULL,
  slug varchar(180) NOT NULL,
  description longtext NULL,
  created_at timestamp NULL DEFAULT current_timestamp(),
  updated_at timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (id),
  CONSTRAINT fk_attr_trans_attr FOREIGN KEY (attraction_id) REFERENCES attractions (id),
  UNIQUE KEY uq_attr_trans_attr_locale (attraction_id, locale),
  KEY idx_attr_trans_locale_slug (locale, slug),
  KEY idx_attr_trans_attr_locale (attraction_id, locale)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE category_translations (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  category_id bigint unsigned NOT NULL,
  locale varchar(8) NOT NULL,
  name varchar(80) NOT NULL,
  slug varchar(64) NOT NULL,
  created_at timestamp NULL DEFAULT current_timestamp(),
  updated_at timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (id),
  CONSTRAINT fk_category_trans_category FOREIGN KEY (category_id) REFERENCES categories (id),
  UNIQUE KEY uq_category_trans_category_locale (category_id, locale),
  UNIQUE KEY uq_category_trans_locale_slug (locale, slug),
  KEY idx_category_trans_locale_slug (locale, slug),
  KEY idx_category_trans_category_locale (category_id, locale)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO country_translations (country_id, locale, name, slug, description, created_at, updated_at)
SELECT id, 'en', name, slug, description, created_at, updated_at
FROM countries
WHERE name IS NOT NULL AND slug IS NOT NULL AND slug <> ''
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  slug = VALUES(slug),
  description = VALUES(description),
  updated_at = VALUES(updated_at);

INSERT INTO city_translations (city_id, locale, name, slug, description, created_at, updated_at)
SELECT id, 'en', name, slug, description, created_at, updated_at
FROM cities
WHERE name IS NOT NULL AND slug IS NOT NULL AND slug <> ''
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  slug = VALUES(slug),
  description = VALUES(description),
  updated_at = VALUES(updated_at);

INSERT INTO attraction_translations (attraction_id, locale, name, slug, description, created_at, updated_at)
SELECT id, 'en', name, slug, description, created_at, updated_at
FROM attractions
WHERE name IS NOT NULL AND slug IS NOT NULL AND slug <> ''
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  slug = VALUES(slug),
  description = VALUES(description),
  updated_at = VALUES(updated_at);

INSERT INTO category_translations (category_id, locale, name, slug, created_at, updated_at)
SELECT id, 'en', name, slug, created_at, updated_at
FROM categories
WHERE name IS NOT NULL AND slug IS NOT NULL AND slug <> ''
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  slug = VALUES(slug),
  updated_at = VALUES(updated_at);

ALTER TABLE search_documents
  ADD COLUMN locale varchar(8) NOT NULL DEFAULT 'en' AFTER entity_id;

ALTER TABLE search_documents
  DROP INDEX uq_search_entity,
  ADD UNIQUE KEY uq_search_entity_locale (entity_type, entity_id, locale),
  ADD KEY idx_search_locale_public_type_rank (locale, is_public, entity_type, rank_score);
