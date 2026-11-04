package com.devlog.auto_retro.devrecord.application.dto;

import java.util.List;

public record DevRecordSummaryPage(

    List<DevRecordSummary> content,
    int page,
    long totalElements,
    int totalPages
) {

    public DevRecordSummaryPage {
        content = List.copyOf(content);
    }
}
