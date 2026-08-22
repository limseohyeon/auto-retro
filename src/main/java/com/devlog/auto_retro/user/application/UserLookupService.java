package com.devlog.auto_retro.user.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.auto_retro.common.error.BusinessException;
import com.devlog.auto_retro.common.error.CommonErrorCode;
import com.devlog.auto_retro.user.application.port.out.UserLookupPort;
import com.devlog.auto_retro.user.error.UserErrorCode;

/**
 * 사용자 ID가 유효하고 실제 저장소에 존재하는지 확인한다.
 */
@Service
@Transactional(readOnly = true)
public class UserLookupService {

	private final UserLookupPort userLookupPort;

	public UserLookupService(UserLookupPort userLookupPort) {
		this.userLookupPort = userLookupPort;
	}

	public UserInfo findById(long userId) {
		if (userId <= 0) {
			throw new BusinessException(CommonErrorCode.INVALID_INPUT);
		}

		if (!userLookupPort.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}

		return new UserInfo(userId);
	}
}
