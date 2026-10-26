package com.devlog.auto_retro.devrecord.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.BDDMockito.given;

import com.devlog.auto_retro.common.error.BusinessException;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordDetail;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordLoadPort;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import com.devlog.auto_retro.devrecord.error.DevRecordErrorCode;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DevRecordDetailServiceTests {

    @Mock
    private DevRecordLoadPort devRecordLoadPort;

    @InjectMocks
    private DevRecordDetailService devRecordDetailService;

    private final long recordId = 1L;
    private final long userId = 1L;
    private final DevRecordDetail devRecordDetail = new DevRecordDetail(
        1L,
        1L,
        "테스트용 제목",
        "작업 내용",
        "작업 중 문제 기록",
        "문제 해결 내용",
        "배운 점",
        DevRecordStatus.EASY,
        LocalDate.of(2026, 10, 13)
    );

    @Test
    void 상세_조회를_성공한다() {
        // given
        given(devRecordLoadPort.loadDetail(recordId)).willReturn(Optional.of(devRecordDetail));

        // when
        DevRecordDetail response = devRecordDetailService.getDetail(recordId, userId);

        // then
        assertThat(response).isEqualTo(devRecordDetail);
    }

    @Test
    void 존재하지_않는_기록의_상세_조회시_실패한다() {
        //given
        given(devRecordLoadPort.loadDetail(recordId)).willReturn(Optional.empty());

        // when
        BusinessException exception = catchThrowableOfType(
            BusinessException.class,
            () -> devRecordDetailService.getDetail(recordId, userId)
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.errorCode())
            .isEqualTo(DevRecordErrorCode.RECORD_NOT_FOUND);
    }

    @Test
    void 본인_소유가_아닌_기록의_상세_조회시_실패한다() {
        //given
        given(devRecordLoadPort.loadDetail(recordId)).willReturn(Optional.of(devRecordDetail));

        // when
        BusinessException exception = catchThrowableOfType(
            BusinessException.class,
            () -> devRecordDetailService.getDetail(recordId, 2L)
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.errorCode())
            .isEqualTo(DevRecordErrorCode.RECORD_NOT_FOUND);
    }
}
