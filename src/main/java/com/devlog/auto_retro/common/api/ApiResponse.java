package com.devlog.auto_retro.common.api;

/**
 * 성공한 API 요청의 공통 응답 형식이다.
 *
 * @param success 요청 성공 여부
 * @param data 응답 데이터
 * @param <T> 응답 데이터 타입
 */
public record ApiResponse<T>(
		boolean success,
		T data
) {

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data);
	}

	public static ApiResponse<Void> successWithoutData() {
		return new ApiResponse<>(true, null);
	}
}
