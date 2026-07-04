package com.travellerguide.traveller_guide_api.interfaces.rest.search;

import com.travellerguide.traveller_guide_api.application.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search index payloads for frontend autocomplete and search pages.")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/v1/api/search/all")
    @Operation(summary = "Get all search items", description = "Returns search items wrapped in an `items` property.")
    @ApiResponse(
            responseCode = "200",
            description = "Search items returned.",
            content = @Content(schema = @Schema(implementation = SearchItemsResponse.class))
    )
    public Map<String, List<SearchItemResponse>> all() {
        return Map.of(
                "items",
                searchService.getAllSearchItems()
        );
    }

    @GetMapping("/v1/api/{locale}/search/all")
    @Operation(summary = "Get localized search items", description = "Returns localized search items wrapped in an `items` property.")
    @ApiResponse(
            responseCode = "200",
            description = "Localized search items returned.",
            content = @Content(schema = @Schema(implementation = SearchItemsResponse.class))
    )
    public Map<String, List<SearchItemResponse>> localizedAll(
            @org.springframework.web.bind.annotation.PathVariable String locale
    ) {
        return Map.of(
                "items",
                searchService.getAllSearchItems(locale)
        );
    }

    private record SearchItemsResponse(List<SearchItemResponse> items) {
    }
}
