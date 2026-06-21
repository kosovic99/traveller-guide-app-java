package com.travellerguide.traveller_guide_api.interfaces.rest.attraction;

import com.travellerguide.traveller_guide_api.application.attraction.AttractionService;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    @GetMapping("/v1/api/cities/{citySlug}/attractions")
    public CityResponse cityAttractions(@PathVariable String citySlug) {
        return attractionService.getCityWithAttractions(citySlug);
    }

    @GetMapping("/v1/api/attractions/{citySlug}/{attractionSlug}")
    public AttractionResponse show(
            @PathVariable String citySlug,
            @PathVariable String attractionSlug
    ) {
        return attractionService.getAttractionByCityAndSlug(citySlug, attractionSlug);
    }

    @RequestMapping(
            value = "/v1/api/attractions/{citySlug}/{attractionSlug}",
            method = RequestMethod.HEAD
    )
    public ResponseEntity<Void> head(
            @PathVariable String citySlug,
            @PathVariable String attractionSlug
    ) {
        if (!attractionService.existsByCityAndSlug(citySlug, attractionSlug)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
