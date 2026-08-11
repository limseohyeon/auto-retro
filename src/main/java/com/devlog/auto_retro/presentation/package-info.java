/**
 * 개발 기록 또는 주간 리포트를 기반으로 발표 초안을 생성한다.

 * <p>원본 데이터는 source type과 source ID로 참조하며, 다른 모듈의 JPA 엔티티와
 * 직접적인 연관관계를 만들지 않는다.</p>
 */
@ApplicationModule(displayName = "Presentation")
package com.devlog.auto_retro.presentation;

import org.springframework.modulith.ApplicationModule;
