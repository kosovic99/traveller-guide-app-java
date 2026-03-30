package com.travellerguide.traveller_guide_api.city;

import com.travellerguide.traveller_guide_api.model.City;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    @EntityGraph(attributePaths = {"country"})
    List<City> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = {"country"})
    Optional<City> findFirstBySlugOrderByIdAsc(String slug);

    @EntityGraph(attributePaths = {"country"})
    List<City> findByCountry_IdOrderByNameAsc(Long countryId);

    @EntityGraph(attributePaths = {"country"})
    List<City> findByCountry_IdAndIdNotOrderByNameAsc(Long countryId, Long excludedCityId);

    long countByCountry_Id(Long countryId);
}