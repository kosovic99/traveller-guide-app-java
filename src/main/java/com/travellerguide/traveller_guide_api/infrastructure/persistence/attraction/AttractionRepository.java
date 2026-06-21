package com.travellerguide.traveller_guide_api.infrastructure.persistence.attraction;

import com.travellerguide.traveller_guide_api.domain.attraction.Attraction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    @EntityGraph(attributePaths = {"city", "city.country"})
    List<Attraction> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = {"city", "city.country"})
    List<Attraction> findByCity_IdOrderByNameAsc(Long cityId);

    @EntityGraph(attributePaths = {"city", "city.country"})
    Optional<Attraction> findByCity_SlugAndSlug(String citySlug, String slug);

    boolean existsByCity_SlugAndSlug(String citySlug, String slug);

    long countByCity_Id(Long cityId);

    long countByCity_Country_Id(Long countryId);
}

