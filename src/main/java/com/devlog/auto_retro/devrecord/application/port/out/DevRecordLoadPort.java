package com.devlog.auto_retro.devrecord.application.port.out;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordDetail;
import java.util.Optional;

public interface DevRecordLoadPort {

    Optional<DevRecordDetail> loadDetail(long recordId);
}
