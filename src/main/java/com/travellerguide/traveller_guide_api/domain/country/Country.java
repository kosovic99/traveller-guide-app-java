package com.travellerguide.traveller_guide_api.domain.country;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "countries",
        indexes = {
                @Index(name = "idx_countries_name", columnList = "name"),
                @Index(name = "idx_countries_slug", columnList = "slug")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_countries_name", columnNames = "name"),
                @UniqueConstraint(name = "uq_countries_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Integer legacyId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", length = 140)
    private String slug;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "flag", columnDefinition = "text")
    private String flag;

    @Column(name = "foto", columnDefinition = "text")
    private String foto;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

