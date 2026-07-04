package com.travellerguide.traveller_guide_api.application.i18n;

import com.travellerguide.traveller_guide_api.interfaces.rest.error.ResourceNotFoundException;

import java.util.Arrays;
import java.util.Locale;

public enum SupportedLocale {
    EN("en"),
    DE("de"),
    IT("it"),
    FR("fr"),
    RU("ru"),
    ZH("zh"),
    SR("sr");

    public static final String DEFAULT = "en";

    private final String code;

    SupportedLocale(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static String normalize(String rawLocale) {
        String code = rawLocale == null || rawLocale.isBlank()
                ? DEFAULT
                : rawLocale.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .map(SupportedLocale::code)
                .filter(code::equals)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Unsupported locale: " + rawLocale));
    }
}
