package com.travellerguide.traveller_guide_api.interfaces.rest.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.travellerguide.traveller_guide_api.domain.category.Category;
import com.travellerguide.traveller_guide_api.domain.city.City;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponse(
        Meta meta,
        List<CategoryItem> categories,
        Map<Long, CityItem> citiesById,
        Map<String, List<Long>> categoryCityIds,
        Map<String, CategoryMeta> categoryMeta
) {

    public static CategoryResponse fromCitySeed(
            String entity,
            int top,
            List<Category> categories,
            List<City> cities,
            Map<String, List<Long>> categoryCityIds,
            Map<String, CategoryMeta> categoryMeta,
            long linksCount
    ) {
        List<CategoryItem> categoryItems = categories.stream()
                .map(CategoryItem::fromEntity)
                .toList();

        Map<Long, CityItem> citiesById = new LinkedHashMap<>();
        for (City city : cities) {
            citiesById.put(city.getId(), CityItem.fromEntity(city));
        }

        Meta meta = new Meta(
                entity,
                top,
                categoryItems.size(),
                citiesById.size(),
                linksCount
        );

        return new CategoryResponse(
                meta,
                categoryItems,
                citiesById,
                categoryCityIds,
                categoryMeta
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Meta(
            String entity,
            Integer top,
            @JsonProperty("categories_count")
            Integer categoriesCount,
            @JsonProperty("cities_count")
            Integer citiesCount,
            @JsonProperty("links_count")
            Long linksCount
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CategoryItem(
            Long id,
            String slug,
            String name,
            @JsonProperty("sort_order")
            Integer sortOrder
    ) {
        public static CategoryItem fromEntity(Category category) {
            return new CategoryItem(
                    category.getId(),
                    category.getSlug(),
                    category.getName(),
                    category.getSortOrder()
            );
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CityItem(
            Long id,
            String name,
            String slug,
            String foto,
            String rating,
            @JsonProperty("country_id")
            Long countryId,
            @JsonProperty("country_name")
            String countryName,
            @JsonProperty("country_slug")
            String countrySlug
    ) {
        public static CityItem fromEntity(City city) {
            return new CityItem(
                    city.getId(),
                    city.getName(),
                    city.getSlug(),
                    city.getFoto(),
                    city.getRating(),
                    city.getCountry() != null ? city.getCountry().getId() : null,
                    city.getCountry() != null ? city.getCountry().getName() : null,
                    city.getCountry() != null ? city.getCountry().getSlug() : null
            );
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CategoryMeta(
            Integer total,
            @JsonProperty("topIds")
            List<Long> topIds
    ) {
    }
}
