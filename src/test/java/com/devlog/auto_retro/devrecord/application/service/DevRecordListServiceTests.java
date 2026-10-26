package com.devlog.auto_retro.devrecord.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordSearchCondition;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummary;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummaryPage;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordLoadPort;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DevRecordListServiceTests {

    @Mock
    private DevRecordLoadPort devRecordLoadPort;

    @InjectMocks
    private DevRecordListService devRecordListService;

    @Test
    void 검색_조건으로_조회한_목록을_반환한다() {
        // given
        DevRecordSearchCondition condition =
            new DevRecordSearchCondition(1L, "JPA", DevRecordStatus.NORMAL, 0);
        DevRecordSummaryPage summaryPage = new DevRecordSummaryPage(
            List.of(new DevRecordSummary(
                1L,
                "JPA 공부",
                DevRecordStatus.NORMAL,
                "작업 내용",
                LocalDate.of(2026, 10, 23)
            )),
            0,
            1L,
            1
        );
        given(devRecordLoadPort.loadPage(condition)).willReturn(summaryPage);

        // when
        DevRecordSummaryPage response = devRecordListService.getList(condition);

        // then
        assertThat(response).isEqualTo(summaryPage);
    }
}
