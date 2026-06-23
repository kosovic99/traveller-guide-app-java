package com.travellerguide.traveller_guide_api.application.city;

import com.travellerguide.traveller_guide_api.domain.city.City;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CitySortTest {

    @Test
    void recommendedSortsByNumericRatingDescendingThenName() {
        City vienna = city("Vienna", "4.9");
        City graz = city("Graz", "4,7");
        City salzburg = city("Salzburg", "4.7");
        City unknown = city("Unknown", null);

        List<String> sortedNames = List.of(unknown, salzburg, vienna, graz).stream()
                .sorted(CitySort.RECOMMENDED.comparator())
                .map(City::getName)
                .toList();

        assertThat(sortedNames).containsExactly("Vienna", "Graz", "Salzburg", "Unknown");
    }

    @Test
    void parsesExternalSortValuesDefensively() {
        assertThat(CitySort.from("rating-desc")).isEqualTo(CitySort.RATING_DESC);
        assertThat(CitySort.from("name")).isEqualTo(CitySort.NAME_ASC);
        assertThat(CitySort.from("unsupported")).isEqualTo(CitySort.RECOMMENDED);
    }

    private static City city(String name, String rating) {
        City city = new City();
        city.setName(name);
        city.setRating(rating);
        return city;
    }
}
