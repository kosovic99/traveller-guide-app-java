package com.travellerguide.traveller_guide_api.interfaces.rest.prerender;

import com.travellerguide.traveller_guide_api.application.prerender.PrerenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Prerender", description = "Route manifests for static generation and prerender jobs.")
public class PrerenderController {

    private final PrerenderService prerenderService;

    @GetMapping("/v1/api/prerender/routes")
    @Operation(summary = "Get prerender routes", description = "Returns all route slugs needed by frontend prerender/static generation.")
    @ApiResponse(
            responseCode = "200",
            description = "Prerender routes returned.",
            content = @Content(schema = @Schema(implementation = PrerenderRoutesResponse.class))
    )
    public PrerenderRoutesResponse routes() {
        return prerenderService.getAllRoutes();
    }

    @GetMapping("/v1/api/{locale}/prerender/routes")
    @Operation(summary = "Get localized prerender routes", description = "Returns localized route slugs needed by frontend prerender/static generation.")
    @ApiResponse(
            responseCode = "200",
            description = "Localized prerender routes returned.",
            content = @Content(schema = @Schema(implementation = PrerenderRoutesResponse.class))
    )
    public PrerenderRoutesResponse localizedRoutes(
            @org.springframework.web.bind.annotation.PathVariable String locale
    ) {
        return prerenderService.getAllRoutes(locale);
    }
}
