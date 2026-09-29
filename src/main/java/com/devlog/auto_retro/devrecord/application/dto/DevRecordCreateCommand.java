package com.devlog.auto_retro.devrecord.application.dto;

import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;

public record DevRecordCreateCommand(

    long userId,
    String title,
    String workContent,
    String problemContent,
    String solutionContent,
    String learnedContent,
    DevRecordStatus status,
    LocalDate recordDate
) {

}
