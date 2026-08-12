package com.devlog.auto_retro.devrecord.error;

import com.devlog.auto_retro.common.error.ErrorCode;

/**
 * 개발 기록 모듈이 소유하는 오류 코드의 확장 지점이다.
 * 실제 오류를 정의할 때 enum으로 전환해 {@link ErrorCode}를 구현한다.
 */
public abstract class DevRecordErrorCode implements ErrorCode {
}
