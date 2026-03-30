package com.travellerguide.traveller_guide_api.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/v1/api/categories")
    public CategoryResponse categorySeed(
            @RequestParam(defaultValue = "city") String entity,
            @RequestParam(defaultValue = "24") Integer top
    ) {
        return categoryService.getCategorySeed(entity, top);
    }
}