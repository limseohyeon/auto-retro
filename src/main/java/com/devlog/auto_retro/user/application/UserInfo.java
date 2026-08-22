package com.devlog.auto_retro.user.application;

/**
 * 조회된 사용자 정보를 애플리케이션 계층에 전달한다.
 *
 * @param userId 사용자 식별자
 */
public record UserInfo(long userId) {
}
