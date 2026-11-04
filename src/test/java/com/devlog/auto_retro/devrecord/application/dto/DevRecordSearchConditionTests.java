package com.devlog.auto_retro.devrecord.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class DevRecordSearchConditionTests {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void 키워드가_없거나_공백이면_검색_조건에서_제외한다(String keyword) {
        // when
        var condition = new DevRecordSearchCondition(1L, keyword, null, 0);

        // then
        assertThat(condition.keyword()).isNull();
    }

    @Test
    void 키워드의_앞뒤_공백을_제거한다() {
        // when
        var condition = new DevRecordSearchCondition(1L, "  JPA ", null, 0);

        // then
        assertThat(condition.keyword()).isEqualTo("JPA");
    }
}
