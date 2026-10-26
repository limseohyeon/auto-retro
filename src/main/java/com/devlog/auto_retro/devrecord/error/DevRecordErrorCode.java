package com.devlog.auto_retro.devrecord.error;

import com.devlog.auto_retro.common.error.ErrorCode;

/**
 * 개발 기록 모듈에서 발생하는 오류를 정의한다.
 */
public enum DevRecordErrorCode implements ErrorCode {

	RECORD_NOT_FOUND(404, "RECORD-001", "개발 기록을 찾을 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;

	DevRecordErrorCode(int status, String code, String message) {
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
