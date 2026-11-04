package com.devlog.auto_retro.devrecord.adapter.in.web.response;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummary;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;
import java.util.List;

public record DevRecordSummaryResponse(

    String recordId,
    String title,
    DevRecordStatus difficulty,
    String workPreview,
    List<String> tags,
    LocalDate recordDate
) {

    public static DevRecordSummaryResponse from(DevRecordSummary summary){
        return new DevRecordSummaryResponse(
            Long.toString(summary.recordId()),
            summary.title(),
            summary.difficulty(),
            summary.workPreview(),
            // TODO: AI 태그 추출 기능 구현 시 기록의 태그 목록으로 채운다.
            List.of(),
            summary.recordDate()
        );
    }
}
