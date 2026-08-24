package com.devlog.auto_retro.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.auto_retro.common.error.BusinessException;
import com.devlog.auto_retro.common.error.CommonErrorCode;
import com.devlog.auto_retro.user.application.dto.UserInfo;
import com.devlog.auto_retro.user.application.port.out.UserLookupPort;
import com.devlog.auto_retro.user.error.UserErrorCode;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 이름이 유효하고 실제 저장소에 존재하는지 확인한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLookupService {

	private final UserLookupPort userLookupPort;

	public UserInfo findByUserNm(String userNm) {
		if (userNm == null || userNm.isBlank()) {
			throw new BusinessException(CommonErrorCode.INVALID_INPUT);
		}

        String normalizedUserNm = userNm.trim();

        return userLookupPort.findByUserNm(normalizedUserNm)
            .orElseThrow(() ->
                new BusinessException(UserErrorCode.USER_NOT_FOUND)
            );

	}
}
