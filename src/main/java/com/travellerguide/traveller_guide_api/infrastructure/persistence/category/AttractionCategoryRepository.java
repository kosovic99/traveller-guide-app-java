package com.travellerguide.traveller_guide_api.infrastructure.persistence.category;

import com.travellerguide.traveller_guide_api.domain.category.AttractionCategory;
import com.travellerguide.traveller_guide_api.domain.category.AttractionCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AttractionCategoryRepository extends JpaRepository<AttractionCategory, AttractionCategoryId> {

    @Query("""
            select ac
            from AttractionCategory ac
            join fetch ac.attraction a
            join fetch a.city c
            join fetch c.country
            join fetch ac.category cat
            where cat.id in :categoryIds
            order by cat.sortOrder asc,
                     cat.name asc,
                     ac.featured desc,
                     ac.score desc,
                     ac.rank asc,
                     a.name asc
            """)
    List<AttractionCategory> findSeedRowsByCategoryIds(Collection<Long> categoryIds);

    @Query("""
            select ac
            from AttractionCategory ac
            join fetch ac.attraction a
            join fetch a.city c
            join fetch c.country
            join fetch ac.category cat
            where cat.id = :categoryId
            order by ac.featured desc,
                     ac.score desc,
                     ac.rank asc,
                     a.name asc
            """)
    List<AttractionCategory> findByCategoryIdForSeed(Long categoryId);
}

