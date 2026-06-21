package com.travellerguide.traveller_guide_api.domain.city;

import com.travellerguide.traveller_guide_api.domain.country.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "cities",
        indexes = {
                @Index(name = "idx_cities_country", columnList = "country_id"),
                @Index(name = "idx_cities_country_name", columnList = "country_id,name"),
                @Index(name = "idx_cities_country_slug", columnList = "country_id,slug"),
                @Index(name = "idx_cities_slug", columnList = "slug")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_cities_country_slug", columnNames = {"country_id", "slug"}),
                @UniqueConstraint(name = "uq_cities_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Integer legacyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "country_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cities_country")
    )
    private Country country;

    @Column(name = "name", nullable = false, length = 180)
    private String name;

    @Column(name = "slug", length = 140)
    private String slug;

    @Column(name = "description", columnDefinition = "longtext")
    private String description;

    @Column(name = "foto", columnDefinition = "text")
    private String foto;

    @Column(name = "rating", length = 50)
    private String rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

