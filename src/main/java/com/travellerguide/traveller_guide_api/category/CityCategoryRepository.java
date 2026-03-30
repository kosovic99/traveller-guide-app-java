package com.travellerguide.traveller_guide_api.category;

import com.travellerguide.traveller_guide_api.model.CityCategory;
import com.travellerguide.traveller_guide_api.model.CityCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CityCategoryRepository extends JpaRepository<CityCategory, CityCategoryId> {

    @Query("""
            select cc
            from CityCategory cc
            join fetch cc.city c
            join fetch c.country
            join fetch cc.category cat
            where cat.id in :categoryIds
            order by cat.sortOrder asc,
                     cat.name asc,
                     cc.featured desc,
                     cc.score desc,
                     cc.rank asc,
                     c.name asc
            """)
    List<CityCategory> findSeedRowsByCategoryIds(Collection<Long> categoryIds);

    @Query("""
            select cc
            from CityCategory cc
            join fetch cc.city c
            join fetch c.country
            join fetch cc.category cat
            where cat.id = :categoryId
            order by cc.featured desc,
                     cc.score desc,
                     cc.rank asc,
                     c.name asc
            """)
    List<CityCategory> findByCategoryIdForSeed(Long categoryId);
}