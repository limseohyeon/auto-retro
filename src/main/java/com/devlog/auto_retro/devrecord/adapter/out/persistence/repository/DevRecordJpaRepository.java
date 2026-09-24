package com.devlog.auto_retro.devrecord.adapter.out.persistence.repository;

import com.devlog.auto_retro.devrecord.adapter.out.persistence.entity.DevRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevRecordJpaRepository extends JpaRepository<DevRecordJpaEntity, Long> {

}
