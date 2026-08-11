/**
 * 개발 기록을 기반으로 한 주간 리포트 생성을 담당한다.

 * <p>수동 요청과 배치 요청은 동일한 애플리케이션 유스케이스를 사용한다. 개발 기록이
 * 필요할 때 {@code devrecord} 모듈의 내부 저장소에 직접 접근하지 않는다.</p>
 */
@ApplicationModule(displayName = "Weekly Report")
package com.devlog.auto_retro.weeklyreport;

import org.springframework.modulith.ApplicationModule;
