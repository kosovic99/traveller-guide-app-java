package com.travellerguide.traveller_guide_api.interfaces.rest.search;

import com.travellerguide.traveller_guide_api.application.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/v1/api/search/all")
    public Map<String, List<SearchItemResponse>> all() {
        return Map.of(
                "items",
                searchService.getAllSearchItems()
        );
    }
}
