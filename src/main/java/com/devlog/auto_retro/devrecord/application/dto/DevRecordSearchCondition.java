package com.devlog.auto_retro.devrecord.application.dto;

import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;

public record DevRecordSearchCondition(

    long userId,
    String keyword,
    DevRecordStatus difficulty,
    int page
) {

    public static final int PAGE_SIZE = 10;
    public static final int MAX_PAGE = 10_000;

    public DevRecordSearchCondition {
        keyword = (keyword == null || keyword.isBlank()) ? null : keyword.strip();
    }
}
