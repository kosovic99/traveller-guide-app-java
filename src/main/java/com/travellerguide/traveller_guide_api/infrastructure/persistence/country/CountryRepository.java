package com.travellerguide.traveller_guide_api.infrastructure.persistence.country;

import com.travellerguide.traveller_guide_api.domain.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByOrderByNameAsc();

    Optional<Country> findBySlug(String slug);

    boolean existsBySlug(String slug);
}

