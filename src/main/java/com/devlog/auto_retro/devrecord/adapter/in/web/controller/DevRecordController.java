package com.devlog.auto_retro.devrecord.adapter.in.web.controller;

import com.devlog.auto_retro.common.api.ApiResponse;
import com.devlog.auto_retro.devrecord.adapter.in.web.request.DevRecordCreateRequest;
import com.devlog.auto_retro.devrecord.adapter.in.web.response.DevRecordCreateResponse;
import com.devlog.auto_retro.devrecord.adapter.in.web.response.DevRecordDetailResponse;
import com.devlog.auto_retro.devrecord.application.service.DevRecordCreateService;
import com.devlog.auto_retro.devrecord.application.service.DevRecordDetailService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dev-records")
public class DevRecordController {
    private final DevRecordCreateService devRecordCreateService;
    private final DevRecordDetailService devRecordDetailService;

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

    // TODO: 인증 기능 구현 시 userId 요청 파라미터를 제거한다.
    @GetMapping("/{recordId}")
    public ApiResponse<DevRecordDetailResponse> getDevRecordDetail(
        @PathVariable("recordId") long recordId,
        @RequestParam("userId") long userId
    ){
        var detail = devRecordDetailService.getDetail(recordId, userId);

        return ApiResponse.success(DevRecordDetailResponse.from(detail));
    }
}
