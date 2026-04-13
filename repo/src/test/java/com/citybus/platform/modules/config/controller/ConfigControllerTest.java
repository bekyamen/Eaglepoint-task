package com.citybus.platform.modules.config.controller;

import com.citybus.platform.modules.config.dto.ConfigDto;
import com.citybus.platform.modules.config.service.ConfigService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigService configService;

    @Test
    void templatesShouldReturnList() throws Exception {
        when(configService.listTemplates()).thenReturn(List.of(new ConfigDto("template.a", "hello", "template")));

        mockMvc.perform(get("/api/v1/config/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].key").value("template.a"));
    }

    @Test
    void dictionariesShouldReturnList() throws Exception {
        when(configService.listDictionaries()).thenReturn(List.of(new ConfigDto("dict.stop", "station", "dictionary")));

        mockMvc.perform(get("/api/v1/config/dictionaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].group").value("dictionary"));
    }
}
