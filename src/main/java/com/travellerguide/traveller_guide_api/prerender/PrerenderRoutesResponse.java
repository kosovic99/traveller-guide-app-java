package com.travellerguide.traveller_guide_api.prerender;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.travellerguide.traveller_guide_api.model.Attraction;
import com.travellerguide.traveller_guide_api.model.Category;
import com.travellerguide.traveller_guide_api.model.City;
import com.travellerguide.traveller_guide_api.model.Country;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PrerenderRoutesResponse(
        @JsonProperty("generated_at")
        LocalDateTime generatedAt,
        Totals totals,
        List<CountryRoute> countries,
        List<CityRoute> cities,
        List<AttractionRoute> attractions,
        List<CategoryRoute> categories
) {

    public static PrerenderRoutesResponse of(
            List<Country> countries,
            List<City> cities,
            List<Attraction> attractions,
            List<Category> categories
    ) {
        List<CountryRoute> countryRoutes = countries.stream()
                .map(CountryRoute::fromEntity)
                .toList();

        List<CityRoute> cityRoutes = cities.stream()
                .map(CityRoute::fromEntity)
                .toList();

        List<AttractionRoute> attractionRoutes = attractions.stream()
                .map(AttractionRoute::fromEntity)
                .toList();

        List<CategoryRoute> categoryRoutes = categories.stream()
                .map(CategoryRoute::fromEntity)
                .toList();

        Totals totals = new Totals(
                countryRoutes.size(),
                cityRoutes.size(),
                attractionRoutes.size(),
                categoryRoutes.size()
        );

        return new PrerenderRoutesResponse(
                LocalDateTime.now(),
                totals,
                countryRoutes,
                cityRoutes,
                attractionRoutes,
                categoryRoutes
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Totals(
            Integer countries,
            Integer cities,
            Integer attractions,
            Integer categories
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CountryRoute(
            @JsonProperty("countrySlug")
            String countrySlug
    ) {
        public static CountryRoute fromEntity(Country country) {
            return new CountryRoute(country.getSlug());
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CityRoute(
            @JsonProperty("citySlug")
            String citySlug
    ) {
        public static CityRoute fromEntity(City city) {
            return new CityRoute(city.getSlug());
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AttractionRoute(
            @JsonProperty("citySlug")
            String citySlug,
            @JsonProperty("attractionSlug")
            String attractionSlug
    ) {
        public static AttractionRoute fromEntity(Attraction attraction) {
            return new AttractionRoute(
                    attraction.getCity() != null ? attraction.getCity().getSlug() : null,
                    attraction.getSlug()
            );
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CategoryRoute(
            @JsonProperty("categorySlug")
            String categorySlug
    ) {
        public static CategoryRoute fromEntity(Category category) {
            return new CategoryRoute(category.getSlug());
        }
    }
}