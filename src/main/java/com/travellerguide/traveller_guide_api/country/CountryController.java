package com.travellerguide.traveller_guide_api.country;

import com.travellerguide.traveller_guide_api.city.CityResponse;
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
    public List<CityResponse> cities(@PathVariable String countrySlug) {
        return countryService.getCitiesByCountrySlug(countrySlug);
    }
}