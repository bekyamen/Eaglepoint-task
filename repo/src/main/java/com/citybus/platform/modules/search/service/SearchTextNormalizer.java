package com.citybus.platform.modules.search.service;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import java.util.Locale;

public final class SearchTextNormalizer {

    private SearchTextNormalizer() {
    }

    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        return input.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String toPinyin(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (Character.toString(ch).matches("[\\u4E00-\\u9FA5]")) {
                try {
                    String[] values = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    if (values != null && values.length > 0) {
                        result.append(values[0]).append(' ');
                    }
                } catch (BadHanyuPinyinOutputFormatCombination ignored) {
                    result.append(ch);
                }
            } else {
                result.append(ch);
            }
        }
        return normalize(result.toString());
    }

    public static String extractInitials(String input) {
        String normalized = normalize(input);
        if (normalized.isEmpty()) {
            return "";
        }
        StringBuilder initials = new StringBuilder();
        for (String token : normalized.split(" ")) {
            if (!token.isEmpty()) {
                initials.append(token.charAt(0));
            }
        }
        return initials.toString();
    }
}
