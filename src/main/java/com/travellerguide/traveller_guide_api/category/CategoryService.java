package com.travellerguide.traveller_guide_api.category;

import com.travellerguide.traveller_guide_api.model.Category;
import com.travellerguide.traveller_guide_api.model.City;
import com.travellerguide.traveller_guide_api.model.CityCategory;
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

    @Cacheable(cacheNames = "categoriesBootstrap", key = "#entity + '::' + #top")
    public CategoryResponse getCategorySeed(String entity, Integer top) {
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

        return CategoryResponse.fromCitySeed(
                resolvedEntity,
                resolvedTop,
                categories,
                new ArrayList<>(citiesById.values()),
                categoryCityIds,
                categoryMeta,
                (long) rows.size()
        );
    }

    private int normalizeTop(Integer top) {
        int value = top == null ? 24 : top;
        return Math.max(1, Math.min(value, 60));
    }
}