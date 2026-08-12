package com.devlog.auto_retro.common.error;

import java.util.List;
import java.util.Objects;

/**
 * 실패한 API 요청의 공통 응답 형식이다.
 *
 * @param success 요청 성공 여부
 * @param status HTTP 상태 코드
 * @param code 애플리케이션 오류 식별 코드
 * @param message 오류 메시지
 * @param fieldErrors 필드별 Validation 오류
 */
public record ErrorResponse(
		boolean success,
		int status,
		String code,
		String message,
		List<FieldError> fieldErrors
) {

	public ErrorResponse {
		fieldErrors = List.copyOf(Objects.requireNonNull(fieldErrors));
	}

	public static ErrorResponse from(ErrorCode errorCode) {
		Objects.requireNonNull(errorCode);

		return new ErrorResponse(
				false,
				errorCode.status(),
				errorCode.code(),
				errorCode.message(),
				List.of()
		);
	}

	public static ErrorResponse from(ErrorCode errorCode, List<FieldError> fieldErrors) {
		Objects.requireNonNull(errorCode);

		return new ErrorResponse(
				false,
				errorCode.status(),
				errorCode.code(),
				errorCode.message(),
				fieldErrors
		);
	}

	/**
	 * 요청 필드 하나의 Validation 실패 정보를 나타낸다.
	 *
	 * @param field 실패한 필드명
	 * @param message 실패 원인
	 */
	public record FieldError(
			String field,
			String message
	) {
	}
}
