-- Non-destructive derived read model for search.
-- Source tables remain authoritative.

CREATE TABLE search_documents (
  id bigint unsigned NOT NULL AUTO_INCREMENT,
  entity_type enum('country','city','attraction') NOT NULL,
  entity_id bigint unsigned NOT NULL,
  title varchar(255) NOT NULL,
  subtitle varchar(255) NULL,
  country_slug varchar(140) NULL,
  city_slug varchar(140) NULL,
  attraction_slug varchar(180) NULL,
  rank_score int unsigned NOT NULL DEFAULT 0,
  is_public tinyint(1) NOT NULL DEFAULT 1,
  updated_at timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (id),
  UNIQUE KEY uq_search_entity (entity_type, entity_id),
  KEY idx_search_public_type_rank (is_public, entity_type, rank_score),
  KEY idx_search_country_slug (country_slug),
  KEY idx_search_city_slug (city_slug),
  FULLTEXT KEY ft_search_title_subtitle (title, subtitle)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
