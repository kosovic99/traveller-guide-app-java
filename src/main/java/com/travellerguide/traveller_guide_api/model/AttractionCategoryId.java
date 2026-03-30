package com.travellerguide.traveller_guide_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AttractionCategoryId implements Serializable {

    @Column(name = "attraction_id", nullable = false)
    private Long attractionId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;
}