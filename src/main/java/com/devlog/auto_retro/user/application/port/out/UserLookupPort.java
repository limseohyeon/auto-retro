package com.devlog.auto_retro.user.application.port.out;

/**
 * 사용자 저장소에 사용자 존재 여부를 질의하는 출력 포트다.
 */
public interface UserLookupPort {

	boolean existsById(long userId);
}
