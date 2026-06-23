package com.travellerguide.traveller_guide_api.interfaces.rest.country;

import com.travellerguide.traveller_guide_api.application.city.CitySort;
import com.travellerguide.traveller_guide_api.application.country.CountryService;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public List<CountryResponse> index() {
        return countryService.getAllCountries();
    }

    @GetMapping("/{countrySlug}")
    public CountryResponse show(@PathVariable String countrySlug) {
        return countryService.getCountryBySlug(countrySlug);
    }

    @GetMapping("/{countrySlug}/cities")
    public List<CityResponse> cities(
            @PathVariable String countrySlug,
            @RequestParam(defaultValue = "recommended") String sort
    ) {
        return countryService.getCitiesByCountrySlug(countrySlug, CitySort.from(sort));
    }
}
