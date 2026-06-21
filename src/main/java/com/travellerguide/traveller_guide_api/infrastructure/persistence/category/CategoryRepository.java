package com.travellerguide.traveller_guide_api.infrastructure.persistence.category;

import com.travellerguide.traveller_guide_api.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByPublicVisibleTrueOrderBySortOrderAscNameAsc();

    Optional<Category> findBySlug(String slug);

    @Query("""
            select c
            from Category c
            where c.publicVisible = true
              and (c.entity = :entity or c.entity = 'both')
            order by c.sortOrder asc, c.name asc
            """)
    List<Category> findPublicByEntityOrBoth(String entity);
}

