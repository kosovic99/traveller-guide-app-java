package com.travellerguide.traveller_guide_api.prerender;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PrerenderController {

    private final PrerenderService prerenderService;

    @GetMapping("/v1/api/prerender/routes")
    public PrerenderRoutesResponse routes() {
        return prerenderService.getAllRoutes();
    }
}