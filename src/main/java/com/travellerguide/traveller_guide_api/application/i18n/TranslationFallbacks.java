package com.travellerguide.traveller_guide_api.application.i18n;

import org.springframework.util.StringUtils;

public final class TranslationFallbacks {

    private TranslationFallbacks() {
    }

    public static TranslationView of(
            String requestedLocale,
            String translatedName,
            String translatedSlug,
            String translatedDescription,
            String baseName,
            String baseSlug,
            String baseDescription
    ) {
        boolean hasTranslation = StringUtils.hasText(translatedName) && StringUtils.hasText(translatedSlug);

        return new TranslationView(
                hasTranslation ? translatedName : baseName,
                hasTranslation ? translatedSlug : baseSlug,
                hasTranslation && translatedDescription != null ? translatedDescription : baseDescription,
                hasTranslation ? requestedLocale : SupportedLocale.DEFAULT,
                requestedLocale,
                !SupportedLocale.DEFAULT.equals(requestedLocale) && !hasTranslation
        );
    }
}
