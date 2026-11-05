package com.devlog.auto_retro.aigeneration.error;

import com.devlog.auto_retro.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AI 생성 모듈이 소유하는 오류 코드의 확장 지점이다.
 * 실제 오류를 정의할 때 enum으로 전환해 {@link ErrorCode}를 구현한다.
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum AiGenerationErrorCode implements ErrorCode {

    INVALID_TAGGING_RESPONSE(
        502,
        "AI-001",
        "AI 태그 응답이 올바르지 않습니다."
    );

    private final int status;
    private final String code;
    private final String message;

}
