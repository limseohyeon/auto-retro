package com.devlog.auto_retro.user.adapter.in.web;

import com.devlog.auto_retro.user.application.UserInfo;

/**
 * 사용자 조회 API의 응답 데이터다.
 *
 * @param userId JavaScript의 정수 범위에 영향을 받지 않는 문자열 ID
 */
public record UserResponse(String userId) {

	public static UserResponse from(UserInfo userInfo) {
		return new UserResponse(Long.toString(userInfo.userId()));
	}
}
