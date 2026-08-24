package com.devlog.auto_retro.user.adapter.out.persistence;

import com.devlog.auto_retro.user.adapter.out.persistence.repository.UserJpaRepository;
import com.devlog.auto_retro.user.application.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.devlog.auto_retro.user.application.port.out.UserLookupPort;

import java.util.Optional;

/**
 * 사용자 조회 출력 포트를 JPA로 구현한다.
 */
@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserLookupPort {

	private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<UserInfo> findByUserNm(String userNm) {
        return userJpaRepository.findByUserNm(userNm)
            .map(entity -> new UserInfo(
                entity.getId(),
                entity.getUserNm()
            ));
    }
}
