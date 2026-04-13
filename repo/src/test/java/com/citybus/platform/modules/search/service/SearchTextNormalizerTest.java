package com.citybus.platform.modules.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SearchTextNormalizerTest {

    @Test
    void shouldConvertChineseToPinyin() {
        String pinyin = SearchTextNormalizer.toPinyin("北京 中央");
        assertTrue(pinyin.contains("bei"));
        assertTrue(pinyin.contains("jing"));
    }

    @Test
    void shouldExtractInitials() {
        assertEquals("bjc", SearchTextNormalizer.extractInitials("Beijing Central Station"));
    }

    @Test
    void normalizeHandlesPunctuationAndSpacing() {
        assertEquals("line 1 north", SearchTextNormalizer.normalize(" Line-1, North! "));
    }

    @Test
    void extractInitialsReturnsTokenInitials() {
        assertEquals("l1n", SearchTextNormalizer.extractInitials("Line 1 North"));
    }
}
