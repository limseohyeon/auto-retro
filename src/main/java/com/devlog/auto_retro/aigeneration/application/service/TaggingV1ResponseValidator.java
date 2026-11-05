package com.devlog.auto_retro.aigeneration.application.service;

import com.devlog.auto_retro.aigeneration.application.dto.RawTag;
import com.devlog.auto_retro.aigeneration.domain.tagging.TagCategory;
import com.devlog.auto_retro.aigeneration.domain.tagging.ValidatedTag;
import com.devlog.auto_retro.aigeneration.error.AiGenerationErrorCode;
import com.devlog.auto_retro.aigeneration.error.AiGenerationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TaggingV1ResponseValidator {

    private static final int MIN_TAG_COUNT = 1;
    private static final int MAX_TAG_COUNT = 5;
    private static final int MAX_NAME_LENGTH = 100;
    private static final Pattern WHITESPACE_PATTERN =
        Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    public List<ValidatedTag> validate(List<RawTag> rawTags) {
        validateResponse(rawTags);

        List<ValidatedTag> validatedTags = new ArrayList<>();

        for (RawTag rawTag : rawTags) {
            ValidatedTag candidate = toValidatedTag(rawTag);

            if (!containsEquivalentTag(validatedTags, candidate)) {
                validatedTags.add(candidate);
            }
        }

        validateTagCount(validatedTags);
        return List.copyOf(validatedTags);
    }

    private void validateResponse(List<RawTag> rawTags) {
        if (rawTags == null || rawTags.isEmpty()) {
            throw invalidResponse();
        }
    }

    private ValidatedTag toValidatedTag(RawTag rawTag) {
        if (rawTag == null) {
            throw invalidResponse();
        }

        ValidatedTag tag = new ValidatedTag(
            cleanName(rawTag.name()),
            parseCategory(rawTag.category())
        );

        validateNameLength(tag.name());
        validateNameLength(tag.normalizedName());

        return tag;
    }

    private String cleanName(String rawName) {
        if (rawName == null) {
            throw invalidResponse();
        }

        String cleanedName = WHITESPACE_PATTERN
            .matcher(rawName)
            .replaceAll(" ")
            .trim();

        if (cleanedName.isEmpty()) {
            throw invalidResponse();
        }

        return cleanedName;
    }

    private void validateNameLength(String name) {
        if (name.codePointCount(0, name.length()) > MAX_NAME_LENGTH) {
            throw invalidResponse();
        }
    }

    private TagCategory parseCategory(String rawCategory) {
        if (rawCategory == null) {
            throw invalidResponse();
        }

        try {
            return TagCategory.valueOf(rawCategory);
        } catch (IllegalArgumentException exception) {
            throw invalidResponse();
        }
    }

    private boolean containsEquivalentTag(
        List<ValidatedTag> validatedTags,
        ValidatedTag candidate
    ) {
        return validatedTags.stream()
            .anyMatch(existing ->
                existing.normalizedName().equals(candidate.normalizedName())
                    && existing.category() == candidate.category()
            );
    }

    private void validateTagCount(List<ValidatedTag> validatedTags) {
        int size = validatedTags.size();

        if (size < MIN_TAG_COUNT || size > MAX_TAG_COUNT) {
            throw invalidResponse();
        }
    }

    private AiGenerationException invalidResponse() {
        return new AiGenerationException(AiGenerationErrorCode.INVALID_TAGGING_RESPONSE);
    }
}
