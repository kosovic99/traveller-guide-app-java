package com.travellerguide.traveller_guide_api.infrastructure.persistence.category;

import com.travellerguide.traveller_guide_api.domain.category.CategoryTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, Long> {

    Optional<CategoryTranslation> findByCategory_IdAndLocale(Long categoryId, String locale);

    List<CategoryTranslation> findByCategory_IdInAndLocale(Collection<Long> categoryIds, String locale);
}
