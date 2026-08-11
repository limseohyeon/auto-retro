/**
 * 개발 기록의 작성, 조회, 수정, 삭제를 담당한다.

 * <p>이 모듈은 {@code dev_records} 테이블을 소유한다. 다른 모듈은 개발 기록 엔티티나
 * 저장소에 직접 접근하지 않고, 개발 기록 ID 또는 공개된 애플리케이션 API를 사용해야 한다.</p>
 */
@ApplicationModule(displayName = "Development Record")
package com.devlog.auto_retro.devrecord;

import org.springframework.modulith.ApplicationModule;
