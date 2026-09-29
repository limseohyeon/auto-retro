package com.devlog.auto_retro.devrecord.adapter.in.web.response;

public record DevRecordCreateResponse(String recordId) {

    public static DevRecordCreateResponse of(long recordId){
        return new DevRecordCreateResponse(Long.toString(recordId));
    }
}
