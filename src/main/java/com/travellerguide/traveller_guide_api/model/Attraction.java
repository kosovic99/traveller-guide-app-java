package com.travellerguide.traveller_guide_api.model;

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
        name = "attractions",
        indexes = {
                @Index(name = "idx_attr_city", columnList = "city_id"),
                @Index(name = "idx_attr_city_name", columnList = "city_id,name"),
                @Index(name = "idx_attr_city_slug", columnList = "city_id,slug"),
                @Index(name = "idx_attr_slug", columnList = "slug")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_attr_city_slug", columnNames = {"city_id", "slug"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "city_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attr_city")
    )
    private City city;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "slug", length = 180)
    private String slug;

    @Column(name = "description", columnDefinition = "longtext")
    private String description;

    @Column(name = "foto", columnDefinition = "text")
    private String foto;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}