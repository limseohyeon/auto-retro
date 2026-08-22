package com.devlog.auto_retro.user.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.devlog.auto_retro.user.application.port.out.UserLookupPort;

/**
 * 사용자 조회 출력 포트를 JPA로 구현한다.
 */
@Repository
public class UserPersistenceAdapter implements UserLookupPort {

	private final UserJpaRepository userJpaRepository;

	public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
		this.userJpaRepository = userJpaRepository;
	}

	@Override
	public boolean existsById(long userId) {
		return userJpaRepository.existsById(userId);
	}
}
