package com.devlog.auto_retro.devrecord.adapter.in.web.response;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummaryPage;
import java.util.List;

public record DevRecordListResponse(

    List<DevRecordSummaryResponse> content,
    int page,
    long totalElements,
    int totalPages
) {

    public static DevRecordListResponse from(DevRecordSummaryPage summaryPage) {
        return new DevRecordListResponse(
            summaryPage.content()
                .stream()
                .map(DevRecordSummaryResponse::from)
                .toList(),
            summaryPage.page(),
            summaryPage.totalElements(),
            summaryPage.totalPages()
        );
    }
}
