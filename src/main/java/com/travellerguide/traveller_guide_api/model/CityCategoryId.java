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
public class CityCategoryId implements Serializable {

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;
}