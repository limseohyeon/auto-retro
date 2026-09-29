package com.devlog.auto_retro.devrecord.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.devlog.auto_retro.devrecord.adapter.out.persistence.entity.DevRecordJpaEntity;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordCreateCommand;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordSavePort;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DevRecordCreateServiceTest {

    @Mock
    private DevRecordSavePort devRecordSavePort;

    @InjectMocks
    private DevRecordCreateService devRecordCreateService;

    @Test
    void 개발_기록_생성한다() {
        // given
        DevRecordCreateCommand command = new DevRecordCreateCommand(
            1l,
            "테스트용 제목",
            "작업 내용",
            "작업 중 문제 기록",
            "문제 해결 내용",
            "배운 점",
            DevRecordStatus.EASY,
            LocalDate.of(2026, 9, 24)
        );

        given(devRecordSavePort.save(command)).willReturn(1l);

        // when
        Long recordId = devRecordCreateService.create(command);

        // then
        assertThat(recordId).isEqualTo(1l);
    }
}
