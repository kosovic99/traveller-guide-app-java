package com.travellerguide.traveller_guide_api.application.search;

import com.travellerguide.traveller_guide_api.application.i18n.SupportedLocale;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationFallbacks;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationView;
import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import com.travellerguide.traveller_guide_api.domain.attraction.AttractionTranslation;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.city.CityTranslation;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import com.travellerguide.traveller_guide_api.domain.country.CountryTranslation;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryTranslationRepository;
import com.travellerguide.traveller_guide_api.interfaces.rest.search.SearchItemMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.search.SearchItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final CountryRepository countryRepository;
    private final CountryTranslationRepository countryTranslationRepository;
    private final CityRepository cityRepository;
    private final CityTranslationRepository cityTranslationRepository;
    private final AttractionRepository attractionRepository;
    private final AttractionTranslationRepository attractionTranslationRepository;
    private final SearchItemMapper searchItemMapper;

    @Cacheable(cacheNames = "searchAll")
    public List<SearchItemResponse> getAllSearchItems() {
        return getAllSearchItems(SupportedLocale.DEFAULT);
    }

    @Cacheable(cacheNames = "searchAll", key = "#locale")
    public List<SearchItemResponse> getAllSearchItems(String locale) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        List<SearchItemResponse> items = new ArrayList<>();

        countryRepository.findAllByOrderByNameAsc().stream()
                .filter(country -> StringUtils.hasText(country.getSlug()))
                .map(country -> fromCountry(country, resolvedLocale))
                .forEach(items::add);

        cityRepository.findAllByOrderByNameAsc().stream()
                .filter(city -> StringUtils.hasText(city.getSlug()))
                .map(city -> fromCity(city, resolvedLocale))
                .forEach(items::add);

        attractionRepository.findAllByOrderByNameAsc().stream()
                .filter(attraction ->
                        StringUtils.hasText(attraction.getSlug())
                                && attraction.getCity() != null
                                && StringUtils.hasText(attraction.getCity().getSlug())
                )
                .map(attraction -> fromAttraction(attraction, resolvedLocale))
                .forEach(items::add);

        items.sort(Comparator.comparing(
                SearchItemResponse::title,
                String.CASE_INSENSITIVE_ORDER
        ));

        return items;
    }

    public List<SearchItemResponse> getAllSearchItemsLegacy() {
        List<SearchItemResponse> items = new ArrayList<>();

        countryRepository.findAllByOrderByNameAsc().stream()
                .filter(country -> StringUtils.hasText(country.getSlug()))
                .map(searchItemMapper::fromCountry)
                .forEach(items::add);

        cityRepository.findAllByOrderByNameAsc().stream()
                .filter(city -> StringUtils.hasText(city.getSlug()))
                .map(searchItemMapper::fromCity)
                .forEach(items::add);

        attractionRepository.findAllByOrderByNameAsc().stream()
                .filter(attraction ->
                        StringUtils.hasText(attraction.getSlug())
                                && attraction.getCity() != null
                                && StringUtils.hasText(attraction.getCity().getSlug())
                )
                .map(searchItemMapper::fromAttraction)
                .forEach(items::add);

        items.sort(Comparator.comparing(SearchItemResponse::title, String.CASE_INSENSITIVE_ORDER));
        return items;
    }

    private SearchItemResponse fromCountry(Country country, String locale) {
        CountryTranslation translation = countryTranslationRepository.findByCountry_IdAndLocale(country.getId(), locale)
                .orElse(null);
        TranslationView countryView = TranslationFallbacks.of(locale, translationName(translation), translationSlug(translation), translationDescription(translation), country.getName(), country.getSlug(), country.getDescription());
        return new SearchItemResponse("country", countryView.name(), null, 0, countryView.name(), countryView.slug(), null, null, null);
    }

    private SearchItemResponse fromCity(City city, String locale) {
        Country country = city.getCountry();
        CityTranslation translation = cityTranslationRepository.findByCity_IdAndLocale(city.getId(), locale).orElse(null);
        TranslationView cityView = TranslationFallbacks.of(locale, translationName(translation), translationSlug(translation), translationDescription(translation), city.getName(), city.getSlug(), city.getDescription());
        TranslationView countryView = country == null ? null : countryTranslationRepository.findByCountry_IdAndLocale(country.getId(), locale)
                .map(countryTranslation -> TranslationFallbacks.of(locale, countryTranslation.getName(), countryTranslation.getSlug(), countryTranslation.getDescription(), country.getName(), country.getSlug(), country.getDescription()))
                .orElseGet(() -> TranslationFallbacks.of(locale, null, null, null, country.getName(), country.getSlug(), country.getDescription()));

        return new SearchItemResponse("city", cityView.name(), countryView != null ? countryView.name() : null, 0, countryView != null ? countryView.name() : null, countryView != null ? countryView.slug() : null, cityView.name(), cityView.slug(), null);
    }

    private SearchItemResponse fromAttraction(Attraction attraction, String locale) {
        City city = attraction.getCity();
        Country country = city != null ? city.getCountry() : null;
        AttractionTranslation translation = attractionTranslationRepository.findByAttraction_IdAndLocale(attraction.getId(), locale).orElse(null);
        TranslationView attractionView = TranslationFallbacks.of(locale, translationName(translation), translationSlug(translation), translationDescription(translation), attraction.getName(), attraction.getSlug(), attraction.getDescription());
        TranslationView cityView = city == null ? null : cityTranslationRepository.findByCity_IdAndLocale(city.getId(), locale)
                .map(cityTranslation -> TranslationFallbacks.of(locale, cityTranslation.getName(), cityTranslation.getSlug(), cityTranslation.getDescription(), city.getName(), city.getSlug(), city.getDescription()))
                .orElseGet(() -> TranslationFallbacks.of(locale, null, null, null, city.getName(), city.getSlug(), city.getDescription()));
        TranslationView countryView = country == null ? null : countryTranslationRepository.findByCountry_IdAndLocale(country.getId(), locale)
                .map(countryTranslation -> TranslationFallbacks.of(locale, countryTranslation.getName(), countryTranslation.getSlug(), countryTranslation.getDescription(), country.getName(), country.getSlug(), country.getDescription()))
                .orElseGet(() -> TranslationFallbacks.of(locale, null, null, null, country.getName(), country.getSlug(), country.getDescription()));

        return new SearchItemResponse("attraction", attractionView.name(), cityView != null ? cityView.name() : null, 0, countryView != null ? countryView.name() : null, countryView != null ? countryView.slug() : null, cityView != null ? cityView.name() : null, cityView != null ? cityView.slug() : null, attractionView.slug());
    }

    private String translationName(CountryTranslation translation) {
        return translation != null ? translation.getName() : null;
    }

    private String translationSlug(CountryTranslation translation) {
        return translation != null ? translation.getSlug() : null;
    }

    private String translationDescription(CountryTranslation translation) {
        return translation != null ? translation.getDescription() : null;
    }

    private String translationName(CityTranslation translation) {
        return translation != null ? translation.getName() : null;
    }

    private String translationSlug(CityTranslation translation) {
        return translation != null ? translation.getSlug() : null;
    }

    private String translationDescription(CityTranslation translation) {
        return translation != null ? translation.getDescription() : null;
    }

    private String translationName(AttractionTranslation translation) {
        return translation != null ? translation.getName() : null;
    }

    private String translationSlug(AttractionTranslation translation) {
        return translation != null ? translation.getSlug() : null;
    }

    private String translationDescription(AttractionTranslation translation) {
        return translation != null ? translation.getDescription() : null;
    }
}

