package com.travellerguide.traveller_guide_api.domain.country;

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
        name = "country_translations",
        indexes = {
                @Index(name = "idx_country_trans_locale_slug", columnList = "locale,slug"),
                @Index(name = "idx_country_trans_country_locale", columnList = "country_id,locale")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_country_trans_country_locale", columnNames = {"country_id", "locale"}),
                @UniqueConstraint(name = "uq_country_trans_locale_slug", columnNames = {"locale", "slug"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CountryTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false, foreignKey = @ForeignKey(name = "fk_country_trans_country"))
    private Country country;

    @Column(name = "locale", nullable = false, length = 8)
    private String locale;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, length = 140)
    private String slug;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
