package com.devlog.auto_retro.devrecord.application.service;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordCreateCommand;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordSavePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DevRecordCreateService {

    private final DevRecordSavePort devRecordSavePort;

    public long create(DevRecordCreateCommand command){
        return devRecordSavePort.save(command);
    }
}
