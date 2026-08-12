package com.devlog.auto_retro.common.error;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 애플리케이션에서 발생한 예외를 공통 API 오류 응답으로 변환한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
		return createErrorResponse(exception.errorCode());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException exception
	) {
		var fieldErrors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(fieldError -> new ErrorResponse.FieldError(
						fieldError.getField(),
						Objects.requireNonNullElse(
								fieldError.getDefaultMessage(),
								"올바르지 않은 값입니다."
						)
				))
				.toList();

		return createErrorResponse(CommonErrorCode.INVALID_INPUT, fieldErrors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
			HttpMessageNotReadableException exception
	) {
		return createErrorResponse(CommonErrorCode.INVALID_INPUT);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException exception
	) {
		var fieldError = new ErrorResponse.FieldError(
				exception.getName(),
				"요청 값의 형식이 올바르지 않습니다."
		);

		return createErrorResponse(CommonErrorCode.INVALID_INPUT, List.of(fieldError));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
			NoResourceFoundException exception
	) {
		return createErrorResponse(CommonErrorCode.API_NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
		log.error("Unexpected server error", exception);

		return createErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
		return ResponseEntity
				.status(errorCode.status())
				.body(ErrorResponse.from(errorCode));
	}

	private ResponseEntity<ErrorResponse> createErrorResponse(
			ErrorCode errorCode,
			List<ErrorResponse.FieldError> fieldErrors
	) {
		return ResponseEntity
				.status(errorCode.status())
				.body(ErrorResponse.from(errorCode, fieldErrors));
	}
}
