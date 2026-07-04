package com.travellerguide.traveller_guide_api.application.prerender;

import com.travellerguide.traveller_guide_api.application.i18n.SupportedLocale;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationFallbacks;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationView;
import com.travellerguide.traveller_guide_api.domain.attraction.AttractionTranslation;
import com.travellerguide.traveller_guide_api.domain.category.CategoryTranslation;
import com.travellerguide.traveller_guide_api.domain.city.CityTranslation;
import com.travellerguide.traveller_guide_api.domain.country.CountryTranslation;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.category.CategoryRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.category.CategoryTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryTranslationRepository;
import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.category.Category;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import com.travellerguide.traveller_guide_api.interfaces.rest.prerender.PrerenderRoutesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrerenderService {

    private final CountryRepository countryRepository;
    private final CountryTranslationRepository countryTranslationRepository;
    private final CityRepository cityRepository;
    private final CityTranslationRepository cityTranslationRepository;
    private final AttractionRepository attractionRepository;
    private final AttractionTranslationRepository attractionTranslationRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;

    @Cacheable(cacheNames = "prerenderRoutes")
    public PrerenderRoutesResponse getAllRoutes() {
        return getAllRoutes(SupportedLocale.DEFAULT);
    }

    @Cacheable(cacheNames = "prerenderRoutes", key = "#locale")
    public PrerenderRoutesResponse getAllRoutes(String locale) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        List<Country> countries = countryRepository.findAllByOrderByNameAsc().stream()
                .filter(country -> StringUtils.hasText(country.getSlug()))
                .toList();

        List<City> cities = cityRepository.findAllByOrderByNameAsc().stream()
                .filter(city -> StringUtils.hasText(city.getSlug()))
                .toList();

        List<Attraction> attractions = attractionRepository.findAllByOrderByNameAsc().stream()
                .filter(attraction ->
                        StringUtils.hasText(attraction.getSlug())
                                && attraction.getCity() != null
                                && StringUtils.hasText(attraction.getCity().getSlug())
                )
                .toList();

        List<Category> categories = categoryRepository.findByPublicVisibleTrueOrderBySortOrderAscNameAsc().stream()
                .filter(category -> StringUtils.hasText(category.getSlug()))
                .toList();

        if (!SupportedLocale.DEFAULT.equals(resolvedLocale)) {
            return localizedResponse(countries, cities, attractions, categories, resolvedLocale);
        }

        return PrerenderRoutesResponse.of(
                countries,
                cities,
                attractions,
                categories
        );
    }

    private PrerenderRoutesResponse localizedResponse(
            List<Country> countries,
            List<City> cities,
            List<Attraction> attractions,
            List<Category> categories,
            String locale
    ) {
        List<PrerenderRoutesResponse.CountryRoute> countryRoutes = countries.stream()
                .map(country -> new PrerenderRoutesResponse.CountryRoute(countrySlug(country, locale)))
                .toList();
        List<PrerenderRoutesResponse.CityRoute> cityRoutes = cities.stream()
                .map(city -> new PrerenderRoutesResponse.CityRoute(citySlug(city, locale)))
                .toList();
        List<PrerenderRoutesResponse.AttractionRoute> attractionRoutes = attractions.stream()
                .map(attraction -> new PrerenderRoutesResponse.AttractionRoute(
                        attraction.getCity() != null ? citySlug(attraction.getCity(), locale) : null,
                        attractionSlug(attraction, locale)
                ))
                .toList();
        List<PrerenderRoutesResponse.CategoryRoute> categoryRoutes = categories.stream()
                .map(category -> new PrerenderRoutesResponse.CategoryRoute(categorySlug(category, locale)))
                .toList();

        PrerenderRoutesResponse.Totals totals = new PrerenderRoutesResponse.Totals(
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

    private String countrySlug(Country country, String locale) {
        CountryTranslation translation = countryTranslationRepository.findByCountry_IdAndLocale(country.getId(), locale)
                .orElse(null);
        return TranslationFallbacks.of(locale, translation != null ? translation.getName() : null, translation != null ? translation.getSlug() : null, translation != null ? translation.getDescription() : null, country.getName(), country.getSlug(), country.getDescription()).slug();
    }

    private String citySlug(City city, String locale) {
        CityTranslation translation = cityTranslationRepository.findByCity_IdAndLocale(city.getId(), locale)
                .orElse(null);
        return TranslationFallbacks.of(locale, translation != null ? translation.getName() : null, translation != null ? translation.getSlug() : null, translation != null ? translation.getDescription() : null, city.getName(), city.getSlug(), city.getDescription()).slug();
    }

    private String attractionSlug(Attraction attraction, String locale) {
        AttractionTranslation translation = attractionTranslationRepository.findByAttraction_IdAndLocale(attraction.getId(), locale)
                .orElse(null);
        TranslationView view = TranslationFallbacks.of(locale, translation != null ? translation.getName() : null, translation != null ? translation.getSlug() : null, translation != null ? translation.getDescription() : null, attraction.getName(), attraction.getSlug(), attraction.getDescription());
        return view.slug();
    }

    private String categorySlug(Category category, String locale) {
        CategoryTranslation translation = categoryTranslationRepository.findByCategory_IdAndLocale(category.getId(), locale)
                .orElse(null);
        return translation != null ? translation.getSlug() : category.getSlug();
    }
}

