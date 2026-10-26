package com.devlog.auto_retro.devrecord.application.port.out;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordDetail;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSearchCondition;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummaryPage;
import java.util.Optional;

public interface DevRecordLoadPort {

    Optional<DevRecordDetail> loadDetail(long recordId);

    DevRecordSummaryPage loadPage(DevRecordSearchCondition condition);
}
