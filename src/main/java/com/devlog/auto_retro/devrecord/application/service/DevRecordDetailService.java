package com.devlog.auto_retro.devrecord.application.service;

import com.devlog.auto_retro.common.error.BusinessException;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordDetail;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordLoadPort;
import com.devlog.auto_retro.devrecord.error.DevRecordErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevRecordDetailService {

    private final DevRecordLoadPort devRecordLoadPort;

    public DevRecordDetail getDetail(long recordId, long userId) {
        return devRecordLoadPort.loadDetail(recordId)
            .filter(detail -> detail.userId() == userId)
            .orElseThrow(() ->
                new BusinessException(DevRecordErrorCode.RECORD_NOT_FOUND)
            );
    }
}
