package com.travellerguide.traveller_guide_api.application.i18n;

public record TranslationView(
        String name,
        String slug,
        String description,
        String locale,
        String requestedLocale,
        boolean fallback
) {
}
