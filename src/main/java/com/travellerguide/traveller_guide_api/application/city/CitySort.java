package com.travellerguide.traveller_guide_api.application.city;

import com.travellerguide.traveller_guide_api.domain.city.City;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Locale;

public enum CitySort {
    RECOMMENDED,
    RATING_DESC,
    NAME_ASC;

    public static CitySort from(String value) {
        if (value == null || value.isBlank()) {
            return RECOMMENDED;
        }

        String normalized = value.trim()
                .replace("-", "_")
                .toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "RATING_DESC", "RATING" -> RATING_DESC;
            case "NAME_ASC", "NAME" -> NAME_ASC;
            case "RECOMMENDED" -> RECOMMENDED;
            default -> RECOMMENDED;
        };
    }

    public Comparator<City> comparator() {
        return switch (this) {
            case NAME_ASC -> byName();
            case RATING_DESC, RECOMMENDED -> byRatingDesc().thenComparing(byName());
        };
    }

    private static Comparator<City> byName() {
        return Comparator.comparing(
                City::getName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
        );
    }

    private static Comparator<City> byRatingDesc() {
        return Comparator.comparing(
                CitySort::ratingScore,
                Comparator.nullsLast(Comparator.reverseOrder())
        );
    }

    private static BigDecimal ratingScore(City city) {
        if (city == null || city.getRating() == null || city.getRating().isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(city.getRating().trim().replace(",", "."));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
