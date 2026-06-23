-- Non-destructive index additions for read-heavy API endpoints.
-- Review production table size and MariaDB online DDL behavior before applying.

CREATE INDEX idx_cities_country_rating_name
ON cities (country_id, rating_score, name);

CREATE INDEX idx_cities_country_popularity_name
ON cities (country_id, popularity_score, name);

CREATE INDEX idx_attractions_city_slug_name
ON attractions (city_id, slug, name);

CREATE INDEX idx_categories_public_entity_sort_slug
ON categories (is_public, entity, sort_order, slug);
