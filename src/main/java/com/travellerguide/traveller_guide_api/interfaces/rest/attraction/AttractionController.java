package com.travellerguide.traveller_guide_api.interfaces.rest.attraction;

import com.travellerguide.traveller_guide_api.application.attraction.AttractionService;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
import com.travellerguide.traveller_guide_api.interfaces.rest.error.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Attractions", description = "Attraction listings and detail pages.")
public class AttractionController {

    private final AttractionService attractionService;

    @GetMapping("/v1/api/cities/{citySlug}/attractions")
    @Operation(summary = "List city attractions", description = "Returns a city with its attraction items.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "City attractions returned.",
                    content = @Content(schema = @Schema(implementation = CityResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public CityResponse cityAttractions(
            @Parameter(description = "City slug.", example = "vienna")
            @PathVariable String citySlug
    ) {
        return attractionService.getCityWithAttractions(citySlug);
    }

    @GetMapping("/v1/api/{locale}/cities/{citySlug}/attractions")
    @Operation(summary = "List localized city attractions", description = "Returns a city with localized attraction items.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localized city attractions returned.",
                    content = @Content(schema = @Schema(implementation = CityResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City or locale not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public CityResponse localizedCityAttractions(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale,
            @Parameter(description = "Localized city slug.", example = "wien")
            @PathVariable String citySlug
    ) {
        return attractionService.getCityWithAttractions(locale, citySlug);
    }

    @GetMapping("/v1/api/attractions/{citySlug}/{attractionSlug}")
    @Operation(summary = "Get attraction by city and slug", description = "Returns one attraction with city and country context.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Attraction returned.",
                    content = @Content(schema = @Schema(implementation = AttractionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Attraction not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public AttractionResponse show(
            @Parameter(description = "City slug.", example = "vienna")
            @PathVariable String citySlug,
            @Parameter(description = "Attraction slug.", example = "schonbrunn-palace")
            @PathVariable String attractionSlug
    ) {
        return attractionService.getAttractionByCityAndSlug(citySlug, attractionSlug);
    }

    @GetMapping("/v1/api/{locale}/attractions/{citySlug}/{attractionSlug}")
    @Operation(summary = "Get localized attraction by city and slug", description = "Returns one localized attraction with city and country context.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localized attraction returned.",
                    content = @Content(schema = @Schema(implementation = AttractionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Attraction or locale not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public AttractionResponse localizedShow(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale,
            @Parameter(description = "Localized city slug.", example = "wien")
            @PathVariable String citySlug,
            @Parameter(description = "Localized attraction slug.", example = "schloss-schoenbrunn")
            @PathVariable String attractionSlug
    ) {
        return attractionService.getAttractionByCityAndSlug(locale, citySlug, attractionSlug);
    }

    @RequestMapping(
            value = "/v1/api/attractions/{citySlug}/{attractionSlug}",
            method = RequestMethod.HEAD
    )
    @Operation(summary = "Check attraction route", description = "Checks whether an attraction detail route exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Attraction exists.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Attraction not found.", content = @Content)
    })
    public ResponseEntity<Void> head(
            @Parameter(description = "City slug.", example = "vienna")
            @PathVariable String citySlug,
            @Parameter(description = "Attraction slug.", example = "schonbrunn-palace")
            @PathVariable String attractionSlug
    ) {
        if (!attractionService.existsByCityAndSlug(citySlug, attractionSlug)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/v1/api/{locale}/attractions/{citySlug}/{attractionSlug}",
            method = RequestMethod.HEAD
    )
    @Operation(summary = "Check localized attraction route", description = "Checks whether a localized attraction detail route exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Localized attraction exists.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Localized attraction not found.", content = @Content)
    })
    public ResponseEntity<Void> localizedHead(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale,
            @Parameter(description = "Localized city slug.", example = "wien")
            @PathVariable String citySlug,
            @Parameter(description = "Localized attraction slug.", example = "schloss-schoenbrunn")
            @PathVariable String attractionSlug
    ) {
        if (!attractionService.existsByCityAndSlug(locale, citySlug, attractionSlug)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
