package com.devlog.auto_retro.devrecord.adapter.out.persistence;

import com.devlog.auto_retro.devrecord.adapter.out.persistence.entity.DevRecordJpaEntity;
import com.devlog.auto_retro.devrecord.adapter.out.persistence.repository.DevRecordJpaRepository;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordCreateCommand;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordSavePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DevRecordPersistenceAdapter implements DevRecordSavePort {

    private final DevRecordJpaRepository devRecordJpaRepository;

    @Override
    public long save(DevRecordCreateCommand command) {
        var entity = DevRecordJpaEntity.create(
            command.userId(),
            command.title(),
            command.workContent(),
            command.problemContent(),
            command.solutionContent(),
            command.learnedContent(),
            command.status(),
            command.recordDate()
        );

        return devRecordJpaRepository
            .save(entity)
            .getId();
    }
}
