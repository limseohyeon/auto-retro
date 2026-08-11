/**
 * AI 요청 실행과 AI 생성 결과 처리를 담당한다.

 * <p>AI 요청 유형, 성공 여부, 응답 시간과 오류 이력을 관리한다. 다른 모듈의 데이터가
 * 필요할 때 해당 모듈의 엔티티나 저장소를 직접 사용하지 않는다.</p>
 */
@ApplicationModule(displayName = "AI Generation")
package com.devlog.auto_retro.aigeneration;

import org.springframework.modulith.ApplicationModule;
