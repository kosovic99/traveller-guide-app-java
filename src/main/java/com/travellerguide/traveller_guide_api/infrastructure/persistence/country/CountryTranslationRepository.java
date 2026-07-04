package com.travellerguide.traveller_guide_api.infrastructure.persistence.country;

import com.travellerguide.traveller_guide_api.domain.country.CountryTranslation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CountryTranslationRepository extends JpaRepository<CountryTranslation, Long> {

    Optional<CountryTranslation> findByCountry_IdAndLocale(Long countryId, String locale);

    @EntityGraph(attributePaths = {"country"})
    Optional<CountryTranslation> findByLocaleAndSlug(String locale, String slug);

    List<CountryTranslation> findByCountry_IdInAndLocale(Collection<Long> countryIds, String locale);
}
