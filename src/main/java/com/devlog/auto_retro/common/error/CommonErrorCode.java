package com.devlog.auto_retro.common.error;

/**
 * 특정 업무 모듈에 속하지 않는 공통 오류를 정의한다.
 */
public enum CommonErrorCode implements ErrorCode {

	INVALID_INPUT(400, "COMMON-001", "입력값이 올바르지 않습니다."),
	API_NOT_FOUND(404, "COMMON-002", "요청한 API를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(500, "COMMON-003", "서버 내부 오류가 발생했습니다.");

	private final int status;
	private final String code;
	private final String message;

	CommonErrorCode(int status, String code, String message) {
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
