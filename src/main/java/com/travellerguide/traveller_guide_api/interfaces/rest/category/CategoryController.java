package com.travellerguide.traveller_guide_api.interfaces.rest.category;

import com.travellerguide.traveller_guide_api.application.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category seed data for frontend discovery pages.")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/v1/api/categories")
    @Operation(summary = "Get category seed data", description = "Returns category metadata and city mappings used by category landing pages.")
    @ApiResponse(
            responseCode = "200",
            description = "Category seed data returned.",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))
    )
    public CategoryResponse categorySeed(
            @Parameter(description = "Entity type for category seed data.", example = "city", schema = @Schema(allowableValues = {"city"}))
            @RequestParam(defaultValue = "city") String entity,
            @Parameter(description = "Maximum number of related city ids per category.", example = "24")
            @RequestParam(defaultValue = "24") Integer top
    ) {
        return categoryService.getCategorySeed(entity, top);
    }

    @GetMapping("/v1/api/{locale}/categories")
    @Operation(summary = "Get localized category seed data", description = "Returns localized category metadata and city mappings.")
    @ApiResponse(
            responseCode = "200",
            description = "Localized category seed data returned.",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))
    )
    public CategoryResponse localizedCategorySeed(
            @Parameter(description = "Locale code.", example = "de")
            @org.springframework.web.bind.annotation.PathVariable String locale,
            @Parameter(description = "Entity type for category seed data.", example = "city", schema = @Schema(allowableValues = {"city"}))
            @RequestParam(defaultValue = "city") String entity,
            @Parameter(description = "Maximum number of related city ids per category.", example = "24")
            @RequestParam(defaultValue = "24") Integer top
    ) {
        return categoryService.getCategorySeed(locale, entity, top);
    }
}
