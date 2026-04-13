package com.citybus.platform.modules.search.controller;

import com.citybus.platform.common.api.ApiResponse;
import com.citybus.platform.modules.search.dto.SearchDto;
import com.citybus.platform.modules.search.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<SearchDto>>> search(@RequestParam("q") String query,
                                                               @RequestParam(value = "type", defaultValue = "all") String type) {
        return ResponseEntity.ok(ApiResponse.<List<SearchDto>>builder()
                .success(true)
                .data(searchService.search(query, type))
                .build());
    }

    @GetMapping("/autocomplete")
    @PreAuthorize("hasAnyRole('PASSENGER','DISPATCHER','ADMIN')")
    public ResponseEntity<ApiResponse<List<SearchDto>>> autocomplete(@RequestParam("q") String query) {
        return ResponseEntity.ok(ApiResponse.<List<SearchDto>>builder()
                .success(true)
                .data(searchService.autocomplete(query))
                .build());
    }
}
