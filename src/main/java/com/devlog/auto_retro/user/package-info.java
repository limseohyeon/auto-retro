/**
 * 사용자 식별과 사용자 데이터의 소유권을 담당한다.
 *
 * <p>이 모듈은 {@code users} 테이블을 소유한다. 다른 모듈은 사용자 엔티티나 저장소에
 * 직접 접근하지 않고, 사용자 ID 또는 이 모듈이 공개한 API를 사용해야 한다.</p>
 */
@ApplicationModule(displayName = "User")
package com.devlog.auto_retro.user;

import org.springframework.modulith.ApplicationModule;
