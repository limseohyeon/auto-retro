package com.devlog.auto_retro.aigeneration.domain.tagging;

import java.util.Locale;

public record ValidatedTag(String name, TagCategory category) {
    public String normalizedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
