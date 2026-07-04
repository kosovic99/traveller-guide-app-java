package com.travellerguide.traveller_guide_api.interfaces.rest.city;

import com.travellerguide.traveller_guide_api.application.city.CitySort;
import com.travellerguide.traveller_guide_api.application.city.CityService;
import com.travellerguide.traveller_guide_api.interfaces.rest.error.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cities", description = "City detail pages and related city lists.")
public class CityController {

    private final CityService cityService;

    @GetMapping("/v1/api/cities/{citySlug}")
    @Operation(summary = "Get city by slug", description = "Returns one city with country and attraction detail data when available.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "City returned.",
                    content = @Content(schema = @Schema(implementation = CityResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public CityResponse show(
            @Parameter(description = "City slug.", example = "vienna")
            @PathVariable String citySlug
    ) {
        return cityService.getCityBySlug(citySlug);
    }

    @GetMapping("/v1/api/{locale}/cities/{citySlug}")
    @Operation(summary = "Get localized city by slug", description = "Returns one city localized for the requested language.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localized city returned.",
                    content = @Content(schema = @Schema(implementation = CityResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City or locale not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public CityResponse localizedShow(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale,
            @Parameter(description = "Localized city slug.", example = "wien")
            @PathVariable String citySlug
    ) {
        return cityService.getCityBySlug(locale, citySlug);
    }

    @GetMapping("/v1/api/cities/{citySlug}/country-cities")
    @Operation(summary = "List cities from the same country", description = "Returns other cities from the country of the selected city.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cities returned.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public List<CityResponse> countryCities(
            @Parameter(description = "City slug used to resolve the country.", example = "vienna")
            @PathVariable String citySlug,
            @Parameter(description = "Sort mode.", example = "name", schema = @Schema(allowableValues = {"recommended", "name"}))
            @RequestParam(defaultValue = "name") String sort
    ) {
        return cityService.getCountryCities(citySlug, CitySort.from(sort));
    }

    @GetMapping("/v1/api/{locale}/cities/{citySlug}/country-cities")
    @Operation(summary = "List localized cities from the same country", description = "Returns related cities localized for the requested language.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localized cities returned.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City or locale not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public List<CityResponse> localizedCountryCities(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale,
            @Parameter(description = "Localized city slug used to resolve the country.", example = "wien")
            @PathVariable String citySlug,
            @Parameter(description = "Sort mode.", example = "name", schema = @Schema(allowableValues = {"recommended", "name"}))
            @RequestParam(defaultValue = "name") String sort
    ) {
        return cityService.getCountryCities(locale, citySlug, CitySort.from(sort));
    }
}
