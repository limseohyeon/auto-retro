package com.devlog.auto_retro.common.error;

/**
 * 애플리케이션 오류 코드가 제공해야 하는 공통 계약이다.
 */
public interface ErrorCode {

	int status();

	String code();

	String message();
}
