package com.travellerguide.traveller_guide_api.interfaces.rest.country;

import com.travellerguide.traveller_guide_api.application.city.CitySort;
import com.travellerguide.traveller_guide_api.application.country.CountryService;
import com.travellerguide.traveller_guide_api.interfaces.rest.city.CityResponse;
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
@Tag(name = "Countries", description = "Country catalogue and country-scoped city lists.")
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/v1/api/countries")
    @Operation(summary = "List countries", description = "Returns all countries available in the Traveller Guide catalogue.")
    @ApiResponse(
            responseCode = "200",
            description = "Countries returned.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryResponse.class)))
    )
    public List<CountryResponse> index() {
        return countryService.getAllCountries();
    }

    @GetMapping("/v1/api/{locale}/countries")
    @Operation(summary = "List countries by locale", description = "Returns all countries localized for the requested language.")
    @ApiResponse(
            responseCode = "200",
            description = "Localized countries returned.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryResponse.class)))
    )
    public List<CountryResponse> localizedIndex(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale
    ) {
        return countryService.getAllCountries(locale);
    }

    @GetMapping("/v1/api/countries/{countrySlug}")
    @Operation(summary = "Get country by slug", description = "Returns country details and aggregate counts for one country.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Country returned.",
                    content = @Content(schema = @Schema(implementation = CountryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public CountryResponse show(
            @Parameter(description = "Country slug.", example = "austria")
            @PathVariable String countrySlug
    ) {
        return countryService.getCountryBySlug(countrySlug);
    }

    @GetMapping("/v1/api/{locale}/countries/{countrySlug}")
    @Operation(summary = "Get localized country by slug", description = "Returns country details in the requested language.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localized country returned.",
                    content = @Content(schema = @Schema(implementation = CountryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country or locale not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public CountryResponse localizedShow(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale,
            @Parameter(description = "Localized country slug.", example = "oesterreich")
            @PathVariable String countrySlug
    ) {
        return countryService.getCountryBySlug(locale, countrySlug);
    }

    @GetMapping("/v1/api/countries/{countrySlug}/cities")
    @Operation(summary = "List cities in a country", description = "Returns cities for one country sorted for catalogue or listing pages.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cities returned.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public List<CityResponse> cities(
            @Parameter(description = "Country slug.", example = "austria")
            @PathVariable String countrySlug,
            @Parameter(description = "Sort mode.", example = "recommended", schema = @Schema(allowableValues = {"recommended", "name"}))
            @RequestParam(defaultValue = "recommended") String sort
    ) {
        return countryService.getCitiesByCountrySlug(countrySlug, CitySort.from(sort));
    }

    @GetMapping("/v1/api/{locale}/countries/{countrySlug}/cities")
    @Operation(summary = "List localized cities in a country", description = "Returns cities for one localized country route.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Localized cities returned.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country or locale not found.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public List<CityResponse> localizedCities(
            @Parameter(description = "Locale code.", example = "de")
            @PathVariable String locale,
            @Parameter(description = "Localized country slug.", example = "oesterreich")
            @PathVariable String countrySlug,
            @Parameter(description = "Sort mode.", example = "recommended", schema = @Schema(allowableValues = {"recommended", "name"}))
            @RequestParam(defaultValue = "recommended") String sort
    ) {
        return countryService.getCitiesByCountrySlug(locale, countrySlug, CitySort.from(sort));
    }
}
