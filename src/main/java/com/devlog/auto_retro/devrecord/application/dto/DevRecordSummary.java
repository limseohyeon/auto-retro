package com.devlog.auto_retro.devrecord.application.dto;

import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;

/**
 * 목록 조회용 개발 기록 요약이다.
 *
 * <p>{@code workContent}는 미리보기 생성에 필요한 앞부분만 담길 수 있다.</p>
 */
public record DevRecordSummary(

    long recordId,
    String title,
    DevRecordStatus difficulty,
    String workContent,
    LocalDate recordDate
) {

    public static final int PREVIEW_LENGTH = 150;
    private static final String ELLIPSIS = "…";

    /**
     * 작업 내용을 code point 기준 최대 150자로 자르고, 잘린 경우 말줄임표를 붙인다.
     */
    public String workPreview() {
        if (workContent.codePointCount(0, workContent.length()) <= PREVIEW_LENGTH) {
            return workContent;
        }
        int end = workContent.offsetByCodePoints(0, PREVIEW_LENGTH);
        return workContent.substring(0, end) + ELLIPSIS;
    }
}
