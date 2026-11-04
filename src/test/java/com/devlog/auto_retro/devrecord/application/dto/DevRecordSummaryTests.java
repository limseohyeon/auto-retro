package com.devlog.auto_retro.devrecord.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DevRecordSummaryTests {

    @Test
    void 작업_내용이_150자_이하이면_원문을_미리보기로_반환한다() {
        // given
        String workContent = "가".repeat(150);

        // when & then
        assertThat(summaryOf(workContent).workPreview()).isEqualTo(workContent);
    }

    @Test
    void 작업_내용이_150자를_넘으면_150자까지_자르고_말줄임표를_붙인다() {
        // given
        String workContent = "가".repeat(150) + "나";

        // when & then
        assertThat(summaryOf(workContent).workPreview()).isEqualTo("가".repeat(150) + "…");
    }

    @Test
    void 이모지는_code_point_단위로_세고_자른다() {
        // given
        String emoji = "😀";
        String workContent = emoji.repeat(151);

        // when
        String preview = summaryOf(workContent).workPreview();

        // then
        assertThat(preview).isEqualTo(emoji.repeat(150) + "…");
        assertThat(preview.codePointCount(0, preview.length())).isEqualTo(151);
    }

    private DevRecordSummary summaryOf(String workContent) {
        return new DevRecordSummary(
            1L,
            "테스트용 제목",
            DevRecordStatus.EASY,
            workContent,
            LocalDate.of(2026, 10, 23)
        );
    }
}
