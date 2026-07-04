package com.travellerguide.traveller_guide_api.infrastructure.persistence.city;

import com.travellerguide.traveller_guide_api.domain.city.CityTranslation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CityTranslationRepository extends JpaRepository<CityTranslation, Long> {

    Optional<CityTranslation> findByCity_IdAndLocale(Long cityId, String locale);

    @EntityGraph(attributePaths = {"city", "city.country"})
    Optional<CityTranslation> findByLocaleAndSlug(String locale, String slug);

    List<CityTranslation> findByCity_IdInAndLocale(Collection<Long> cityIds, String locale);
}
