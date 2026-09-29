package com.devlog.auto_retro.devrecord.application.port.out;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordCreateCommand;

public interface DevRecordSavePort {

    long save(DevRecordCreateCommand command);
}
