package com.travellerguide.traveller_guide_api.domain.category;

import com.travellerguide.traveller_guide_api.domain.city.City;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "city_categories",
        indexes = {
                @Index(name = "idx_city_cat_category_pick", columnList = "category_id,is_featured,score,rank,city_id"),
                @Index(name = "idx_city_cat_city", columnList = "city_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CityCategory {

    @EmbeddedId
    private CityCategoryId id = new CityCategoryId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("cityId")
    @JoinColumn(
            name = "city_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_city_categories_city")
    )
    private City city;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("categoryId")
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_city_categories_category")
    )
    private Category category;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "is_featured", nullable = false)
    private boolean featured = false;

    @Column(name = "rank", nullable = false)
    private Integer rank = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

