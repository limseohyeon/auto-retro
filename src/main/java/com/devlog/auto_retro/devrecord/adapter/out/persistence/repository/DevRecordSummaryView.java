package com.devlog.auto_retro.devrecord.adapter.out.persistence.repository;

import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;

public interface DevRecordSummaryView {

    Long getRecordId();

    String getTitle();

    DevRecordStatus getDifficulty();

    String getWorkContent();

    LocalDate getRecordDate();
}
