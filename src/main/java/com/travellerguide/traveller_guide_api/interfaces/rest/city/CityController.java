package com.travellerguide.traveller_guide_api.interfaces.rest.city;

import com.travellerguide.traveller_guide_api.application.city.CitySort;
import com.travellerguide.traveller_guide_api.application.city.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping("/{citySlug}")
    public CityResponse show(@PathVariable String citySlug) {
        return cityService.getCityBySlug(citySlug);
    }

    @GetMapping("/{citySlug}/country-cities")
    public List<CityResponse> countryCities(
            @PathVariable String citySlug,
            @RequestParam(defaultValue = "name") String sort
    ) {
        return cityService.getCountryCities(citySlug, CitySort.from(sort));
    }
}
