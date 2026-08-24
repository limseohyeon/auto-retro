package com.devlog.auto_retro.user.adapter.out.persistence.repository;

import com.devlog.auto_retro.user.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 JPA 엔티티의 데이터 접근을 담당한다.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
}
