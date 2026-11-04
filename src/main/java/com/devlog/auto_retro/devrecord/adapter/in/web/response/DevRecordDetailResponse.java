package com.devlog.auto_retro.devrecord.adapter.in.web.response;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordDetail;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;

public record DevRecordDetailResponse(

    String recordId,
    String title,
    String workContent,
    String problemContent,
    String solutionContent,
    String learnedContent,
    DevRecordStatus status,
    LocalDate recordDate
) {

    public static DevRecordDetailResponse from(DevRecordDetail detail){
        return new DevRecordDetailResponse(
            Long.toString(detail.recordId()),
            detail.title(),
            detail.workContent(),
            detail.problemContent(),
            detail.solutionContent(),
            detail.learnedContent(),
            detail.status(),
            detail.recordDate()
        );
    }
}
