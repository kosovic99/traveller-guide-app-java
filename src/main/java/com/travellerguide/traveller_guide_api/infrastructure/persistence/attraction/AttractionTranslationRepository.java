package com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction;

import com.travellerguide.traveller_guide_api.domain.attraction.AttractionTranslation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AttractionTranslationRepository extends JpaRepository<AttractionTranslation, Long> {

    Optional<AttractionTranslation> findByAttraction_IdAndLocale(Long attractionId, String locale);

    @EntityGraph(attributePaths = {"attraction", "attraction.city", "attraction.city.country"})
    Optional<AttractionTranslation> findByLocaleAndAttraction_City_IdAndSlug(String locale, Long cityId, String slug);

    List<AttractionTranslation> findByAttraction_IdInAndLocale(Collection<Long> attractionIds, String locale);
}
