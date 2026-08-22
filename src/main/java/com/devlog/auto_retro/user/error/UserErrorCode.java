package com.devlog.auto_retro.user.error;

import com.devlog.auto_retro.common.error.ErrorCode;

/**
 * 사용자 모듈에서 발생하는 오류를 정의한다.
 */
public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND(404, "USER-001", "사용자를 찾을 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;

	UserErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	@Override
	public int status() {
		return status;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}
}
