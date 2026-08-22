package com.devlog.auto_retro.user.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * {@code users} 테이블을 JPA에 매핑한다.
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	protected UserJpaEntity() {
	}

	public Long getId() {
		return id;
	}
}
