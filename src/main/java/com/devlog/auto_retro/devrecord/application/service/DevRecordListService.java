package com.devlog.auto_retro.devrecord.application.service;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordSearchCondition;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummaryPage;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordLoadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevRecordListService {

    private final DevRecordLoadPort devRecordLoadPort;

    public DevRecordSummaryPage getList(DevRecordSearchCondition condition) {
        return devRecordLoadPort.loadPage(condition);
    }
}
