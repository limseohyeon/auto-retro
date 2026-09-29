package com.devlog.auto_retro.devrecord.adapter.in.web.request;

import com.devlog.auto_retro.devrecord.application.dto.DevRecordCreateCommand;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DevRecordCreateRequest(

    @NotNull
    Long userId,

    @NotBlank
    String title,

    @NotBlank
    String workContent,

    String problemContent,

    String solutionContent,

    String learnedContent,

    @NotNull
    DevRecordStatus status,

    @NotNull
    LocalDate recordDate
) {

    public DevRecordCreateCommand toCommand(){
        return new DevRecordCreateCommand(
            userId,
            title,
            workContent,
            problemContent,
            solutionContent,
            learnedContent,
            status,
            recordDate
        );
    }
}
