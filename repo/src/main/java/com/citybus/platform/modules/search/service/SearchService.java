package com.citybus.platform.modules.search.service;

import com.citybus.platform.modules.search.dto.SearchDto;
import java.util.List;

public interface SearchService {
    List<SearchDto> search(String query, String type);

    List<SearchDto> autocomplete(String query);
}
