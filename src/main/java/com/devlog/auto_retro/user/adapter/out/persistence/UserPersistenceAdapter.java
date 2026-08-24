package com.devlog.auto_retro.user.adapter.out.persistence;

import com.devlog.auto_retro.user.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import com.devlog.auto_retro.user.application.port.out.UserLookupPort;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 조회 출력 포트를 JPA로 구현한다.
 */
@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserLookupPort {

	private final UserJpaRepository userJpaRepository;

	@Override
	public boolean existsById(long userId) {
		return userJpaRepository.existsById(userId);
	}
}
