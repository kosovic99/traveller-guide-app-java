package com.travellerguide.traveller_guide_api.application.category;

import com.travellerguide.traveller_guide_api.application.i18n.SupportedLocale;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationFallbacks;
import com.travellerguide.traveller_guide_api.application.i18n.TranslationView;
import com.travellerguide.traveller_guide_api.domain.category.Category;
import com.travellerguide.traveller_guide_api.domain.category.CategoryTranslation;
import com.travellerguide.traveller_guide_api.domain.city.City;
import com.travellerguide.traveller_guide_api.domain.city.CityTranslation;
import com.travellerguide.traveller_guide_api.domain.category.CityCategory;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.category.CategoryRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.category.CategoryTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.category.CityCategoryRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.city.CityTranslationRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryTranslationRepository;
import com.travellerguide.traveller_guide_api.interfaces.rest.category.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CityCategoryRepository cityCategoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;
    private final CityTranslationRepository cityTranslationRepository;
    private final CountryTranslationRepository countryTranslationRepository;

    @Cacheable(cacheNames = "categoriesBootstrap", key = "#entity + '::' + #top")
    public CategoryResponse getCategorySeed(String entity, Integer top) {
        return getCategorySeed(SupportedLocale.DEFAULT, entity, top);
    }

    @Cacheable(cacheNames = "categoriesBootstrap", key = "#locale + '::' + #entity + '::' + #top")
    public CategoryResponse getCategorySeed(String locale, String entity, Integer top) {
        String resolvedLocale = SupportedLocale.normalize(locale);
        String resolvedEntity = "city";
        int resolvedTop = normalizeTop(top);

        List<Category> categories = categoryRepository.findPublicByEntityOrBoth(resolvedEntity);

        Map<String, List<Long>> categoryCityIds = new LinkedHashMap<>();
        Map<String, CategoryResponse.CategoryMeta> categoryMeta = new LinkedHashMap<>();
        Map<Long, City> citiesById = new LinkedHashMap<>();
        Map<Long, String> categoryIdToSlug = new LinkedHashMap<>();
        Map<String, Set<Long>> seenInCategory = new LinkedHashMap<>();

        for (Category category : categories) {
            categoryIdToSlug.put(category.getId(), category.getSlug());
            categoryCityIds.put(category.getSlug(), new ArrayList<>());
            seenInCategory.put(category.getSlug(), new HashSet<>());
        }

        if (categories.isEmpty()) {
            return CategoryResponse.fromCitySeed(
                    resolvedEntity,
                    resolvedTop,
                    categories,
                    List.of(),
                    categoryCityIds,
                    categoryMeta,
                    0L
            );
        }

        List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .toList();

        List<CityCategory> rows = cityCategoryRepository.findSeedRowsByCategoryIds(categoryIds);

        for (CityCategory row : rows) {
            if (row.getCategory() == null || row.getCity() == null) {
                continue;
            }

            String categorySlug = categoryIdToSlug.get(row.getCategory().getId());
            if (categorySlug == null) {
                continue;
            }

            City city = row.getCity();
            Long cityId = city.getId();
            if (cityId == null) {
                continue;
            }

            citiesById.putIfAbsent(cityId, city);

            if (seenInCategory.get(categorySlug).add(cityId)) {
                categoryCityIds.get(categorySlug).add(cityId);
            }
        }

        for (Category category : categories) {
            String slug = category.getSlug();
            List<Long> ids = categoryCityIds.getOrDefault(slug, List.of());

            categoryMeta.put(
                    slug,
                    new CategoryResponse.CategoryMeta(
                            ids.size(),
                            ids.stream().limit(resolvedTop).toList()
                    )
            );
        }

        return toLocalizedCategoryResponse(
                resolvedEntity,
                resolvedTop,
                categories,
                new ArrayList<>(citiesById.values()),
                categoryCityIds,
                categoryMeta,
                rows.size(),
                resolvedLocale
        );
    }

    private int normalizeTop(Integer top) {
        int value = top == null ? 24 : top;
        return Math.max(1, Math.min(value, 60));
    }

    private CategoryResponse toLocalizedCategoryResponse(
            String entity,
            int top,
            List<Category> categories,
            List<City> cities,
            Map<String, List<Long>> categoryCityIds,
            Map<String, CategoryResponse.CategoryMeta> categoryMeta,
            long linksCount,
            String locale
    ) {
        List<CategoryResponse.CategoryItem> categoryItems = categories.stream()
                .map(category -> toCategoryItem(category, locale))
                .toList();

        Map<Long, CategoryResponse.CityItem> citiesById = new LinkedHashMap<>();
        for (City city : cities) {
            citiesById.put(city.getId(), toCityItem(city, locale));
        }

        CategoryResponse.Meta meta = new CategoryResponse.Meta(
                entity,
                top,
                categoryItems.size(),
                citiesById.size(),
                linksCount
        );

        return new CategoryResponse(meta, categoryItems, citiesById, categoryCityIds, categoryMeta);
    }

    private CategoryResponse.CategoryItem toCategoryItem(Category category, String locale) {
        CategoryTranslation translation = categoryTranslationRepository
                .findByCategory_IdAndLocale(category.getId(), locale)
                .orElse(null);

        String name = translation != null ? translation.getName() : category.getName();
        String slug = translation != null ? translation.getSlug() : category.getSlug();

        return new CategoryResponse.CategoryItem(category.getId(), slug, name, category.getSortOrder());
    }

    private CategoryResponse.CityItem toCityItem(City city, String locale) {
        CityTranslation translation = cityTranslationRepository.findByCity_IdAndLocale(city.getId(), locale)
                .orElse(null);
        TranslationView cityView = TranslationFallbacks.of(
                locale,
                translation != null ? translation.getName() : null,
                translation != null ? translation.getSlug() : null,
                translation != null ? translation.getDescription() : null,
                city.getName(),
                city.getSlug(),
                city.getDescription()
        );

        TranslationView countryView = city.getCountry() == null
                ? null
                : countryTranslationRepository.findByCountry_IdAndLocale(city.getCountry().getId(), locale)
                .map(countryTranslation -> TranslationFallbacks.of(
                        locale,
                        countryTranslation.getName(),
                        countryTranslation.getSlug(),
                        countryTranslation.getDescription(),
                        city.getCountry().getName(),
                        city.getCountry().getSlug(),
                        city.getCountry().getDescription()
                ))
                .orElseGet(() -> TranslationFallbacks.of(
                        locale,
                        null,
                        null,
                        null,
                        city.getCountry().getName(),
                        city.getCountry().getSlug(),
                        city.getCountry().getDescription()
                ));

        return new CategoryResponse.CityItem(
                city.getId(),
                cityView.name(),
                cityView.slug(),
                city.getFoto(),
                city.getRating(),
                city.getCountry() != null ? city.getCountry().getId() : null,
                countryView != null ? countryView.name() : null,
                countryView != null ? countryView.slug() : null
        );
    }
}

