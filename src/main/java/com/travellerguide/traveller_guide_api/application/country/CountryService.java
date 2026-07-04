package com.travellerguide.traveller_guide_api.application.country;

import com.travellerguide.traveller_guide_api.application.i18n.SupportedLocale;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationFallbacks;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationView;
import com.travellerguide.traveller_guide_api.domain.country.CountryTranslation;
import com.travellerguide.traveller_guide_api.domain.city.CityTranslation;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction.AttractionRepository;
import com.travellerguide.traveller_guide_api.application.city.CitySort;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryTranslationRepository;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.country.CountryMapper;
import com.travellerguide.traveller_guide_api.interfaces.rest.country.CountryResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.error.ResourceNotFoundException;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.country.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryTranslationRepository countryTranslationRepository;
    private final CityRepository cityRepository;
    private final CityTranslationRepository cityTranslationRepository;
    private final AttractionRepository attractionRepository;
    private final CountryMapper countryMapper;
    private final CityMapper cityMapper;

    @Cacheable(cacheNames = "countries")
    public List<CountryResponse> getAllCountries() {
        return getAllCountries(SupportedLocale.DEFAULT);
    }

    @Cacheable(cacheNames = "countries", key = "#locale")
    public List<CountryResponse> getAllCountries(String locale) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        List<Country> countries = countryRepository.findAllByOrderByNameAsc();
        Map<Long, CountryTranslation> translations = countryTranslationRepository.findByCountry_IdInAndLocale(
                        countries.stream().map(Country::getId).toList(),
                        resolvedLocale
                ).stream()
                .collect(Collectors.toMap(translation -> translation.getCountry().getId(), Function.identity()));

        return countries.stream()
                .map(country -> toLocalizedCountryResponse(country, translations.get(country.getId()),
                        resolvedLocale,
                        cityRepository.countByCountry_Id(country.getId()),
                        attractionRepository.countByCity_Country_Id(country.getId())))
                .toList();
    }

    public List<CountryResponse> getAllCountriesLegacy() {
        return countryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(country -> countryMapper.toResponse(
                        country,
                        cityRepository.countByCountry_Id(country.getId()),
                        attractionRepository.countByCity_Country_Id(country.getId())
                ))
                .toList();
    }

    @Cacheable(cacheNames = "countryBySlug", key = "#slug")
    public CountryResponse getCountryBySlug(String slug) {
        return getCountryBySlug(SupportedLocale.DEFAULT, slug);
    }

    @Cacheable(cacheNames = "countryBySlug", key = "#locale + '::' + #slug")
    public CountryResponse getCountryBySlug(String locale, String slug) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        Country country = findCountryBySlugOrThrow(resolvedLocale, slug);
        CountryTranslation translation = countryTranslationRepository
                .findByCountry_IdAndLocale(country.getId(), resolvedLocale)
                .orElse(null);

        return toLocalizedCountryResponse(country, translation, resolvedLocale, null, null);
    }

    public CountryResponse getCountryBySlugLegacy(String slug) {
        Country country = findCountryBySlugOrThrow(slug);
        return countryMapper.toResponse(country);
    }

    @Cacheable(cacheNames = "citiesByCountrySlug", key = "#countrySlug + '::' + #sort")
    public List<CityResponse> getCitiesByCountrySlug(String countrySlug, CitySort sort) {
        return getCitiesByCountrySlug(SupportedLocale.DEFAULT, countrySlug, sort);
    }

    @Cacheable(cacheNames = "citiesByCountrySlug", key = "#locale + '::' + #countrySlug + '::' + #sort")
    public List<CityResponse> getCitiesByCountrySlug(String locale, String countrySlug, CitySort sort) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        Country country = findCountryBySlugOrThrow(resolvedLocale, countrySlug);

        List<City> cities = cityRepository.findByCountry_IdOrderByNameAsc(country.getId());

        return cities.stream()
                .sorted(sort == null ? CitySort.RECOMMENDED.comparator() : sort.comparator())
                .map(city -> {
                    CityTranslation translation = cityTranslationRepository
                            .findByCity_IdAndLocale(city.getId(), resolvedLocale)
                            .orElse(null);
                    return toLocalizedCityResponse(
                            city,
                            translation,
                            resolvedLocale,
                            attractionRepository.countByCity_Id(city.getId())
                    );
                })
                .toList();
    }

    public List<CityResponse> getCitiesByCountrySlugLegacy(String countrySlug, CitySort sort) {
        Country country = findCountryBySlugOrThrow(countrySlug);

        List<City> cities = cityRepository.findByCountry_IdOrderByNameAsc(country.getId());

        return cities.stream()
                .sorted(sort == null ? CitySort.RECOMMENDED.comparator() : sort.comparator())
                .map(city -> cityMapper.toResponse(
                        city,
                        attractionRepository.countByCity_Id(city.getId())
                ))
                .toList();
    }

    private Country findCountryBySlugOrThrow(String locale, String slug) {
        if (!SupportedLocale.DEFAULT.equals(locale)) {
            return countryTranslationRepository.findByLocaleAndSlug(locale, slug)
                    .map(CountryTranslation::getCountry)
                    .orElseGet(() -> findCountryBySlugOrThrow(slug));
        }

        return findCountryBySlugOrThrow(slug);
    }

    private Country findCountryBySlugOrThrow(String slug) {
        return countryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country not found for slug: " + slug
                ));
    }

    private CountryResponse toLocalizedCountryResponse(
            Country country,
            CountryTranslation translation,
            String locale,
            Long cityCount,
            Long attractionCount
    ) {
        TranslationView view = TranslationFallbacks.of(
                locale,
                translation != null ? translation.getName() : null,
                translation != null ? translation.getSlug() : null,
                translation != null ? translation.getDescription() : null,
                country.getName(),
                country.getSlug(),
                country.getDescription()
        );

        return new CountryResponse(
                country.getId(),
                view.name(),
                view.slug(),
                country.getFoto(),
                country.getFlag(),
                view.description(),
                cityCount,
                attractionCount
        );
    }

    private CityResponse toLocalizedCityResponse(
            City city,
            CityTranslation translation,
            String locale,
            Long attractionCount
    ) {
        TranslationView view = TranslationFallbacks.of(
                locale,
                translation != null ? translation.getName() : null,
                translation != null ? translation.getSlug() : null,
                translation != null ? translation.getDescription() : null,
                city.getName(),
                city.getSlug(),
                city.getDescription()
        );

        return new CityResponse(
                city.getId(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                view.name(),
                view.slug(),
                city.getFoto(),
                city.getRating(),
                view.description(),
                attractionCount,
                null,
                null
        );
    }
}

