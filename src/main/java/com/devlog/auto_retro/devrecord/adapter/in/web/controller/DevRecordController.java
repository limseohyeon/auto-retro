package com.devlog.auto_retro.devrecord.adapter.in.web.controller;

import com.devlog.auto_retro.common.api.ApiResponse;
import com.devlog.auto_retro.devrecord.adapter.in.web.request.DevRecordCreateRequest;
import com.devlog.auto_retro.devrecord.adapter.in.web.response.DevRecordCreateResponse;
import com.devlog.auto_retro.devrecord.application.service.DevRecordCreateService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dev-records")
public class DevRecordController {
    private final DevRecordCreateService devRecordCreateService;

    @PostMapping
    public ResponseEntity<ApiResponse<DevRecordCreateResponse>> createDevRecord(
        @Valid @RequestBody DevRecordCreateRequest request,
        UriComponentsBuilder uriComponentsBuilder
    ){
        long recordId = devRecordCreateService.create(request.toCommand());
        URI location = uriComponentsBuilder
            .path("/api/dev-records/{recordId}")
            .buildAndExpand(recordId)
            .toUri();
        return ResponseEntity
                        .created(location)
                        .body(ApiResponse.success(DevRecordCreateResponse.of(recordId)));
    }
}
