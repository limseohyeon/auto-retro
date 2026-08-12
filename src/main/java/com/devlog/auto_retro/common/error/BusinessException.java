package com.devlog.auto_retro.common.error;

import java.util.Objects;

/**
 * 비즈니스 규칙 위반을 나타내는 공통 예외다.
 */
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(Objects.requireNonNull(errorCode).message());
		this.errorCode = errorCode;
	}

	public ErrorCode errorCode() {
		return errorCode;
	}
}
