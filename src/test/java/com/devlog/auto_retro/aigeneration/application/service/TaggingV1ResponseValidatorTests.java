package com.devlog.auto_retro.aigeneration.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devlog.auto_retro.aigeneration.application.dto.RawTag;
import com.devlog.auto_retro.aigeneration.domain.tagging.TagCategory;
import com.devlog.auto_retro.aigeneration.domain.tagging.ValidatedTag;
import com.devlog.auto_retro.aigeneration.error.AiGenerationErrorCode;
import com.devlog.auto_retro.aigeneration.error.AiGenerationException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaggingV1ResponseValidatorTests {

    private final TaggingV1ResponseValidator validator = new TaggingV1ResponseValidator();

    @Test
    void 태그명의_앞뒤와_연속_공백을_정리한다() {
        List<RawTag> rawTags = List.of(
            new RawTag(" Spring   Boot ", "FRAMEWORK")
        );

        List<ValidatedTag> result = validator.validate(rawTags);

        assertThat(result).containsExactly(
            new ValidatedTag("Spring Boot", TagCategory.FRAMEWORK)
        );
        assertThat(result.getFirst().normalizedName()).isEqualTo("spring boot");
    }

    @Test
    void 유니코드_공백을_일반_공백으로_정리한다() {
        List<RawTag> rawTags = List.of(
            new RawTag("Spring\u00A0\u2003Boot", "FRAMEWORK")
        );

        List<ValidatedTag> result = validator.validate(rawTags);

        assertThat(result.getFirst().name()).isEqualTo("Spring Boot");
    }

    @Test
    void 동일한_정규화_이름과_카테고리의_중복을_제거한다() {
        List<RawTag> rawTags = List.of(
            new RawTag("Spring Boot", "FRAMEWORK"),
            new RawTag(" spring   boot ", "FRAMEWORK")
        );

        List<ValidatedTag> result = validator.validate(rawTags);

        assertThat(result).containsExactly(
            new ValidatedTag("Spring Boot", TagCategory.FRAMEWORK)
        );
    }

    @Test
    void 정규화_이름이_같아도_카테고리가_다르면_유지한다() {
        List<RawTag> rawTags = List.of(
            new RawTag("Java", "LANGUAGE"),
            new RawTag("java", "ETC")
        );

        List<ValidatedTag> result = validator.validate(rawTags);

        assertThat(result).containsExactly(
            new ValidatedTag("Java", TagCategory.LANGUAGE),
            new ValidatedTag("java", TagCategory.ETC)
        );
    }

    @Test
    void 태그_5개를_허용한다() {
        List<ValidatedTag> result = validator.validate(fiveDistinctTags());

        assertThat(result).hasSize(5);
    }

    @Test
    void 태그_6개를_거부한다() {
        List<RawTag> rawTags = List.of(
            new RawTag("Java", "LANGUAGE"),
            new RawTag("Spring Boot", "FRAMEWORK"),
            new RawTag("PostgreSQL", "DB"),
            new RawTag("Docker", "INFRA"),
            new RawTag("N+1", "ISSUE"),
            new RawTag("Git", "ETC")
        );

        assertInvalid(rawTags);
    }

    @Test
    void 길이가_100자인_태그명을_허용한다() {
        List<ValidatedTag> result = validator.validate(
            List.of(new RawTag("a".repeat(100), "ETC"))
        );

        assertThat(result.getFirst().name()).hasSize(100);
    }

    @Test
    void null_응답을_거부한다() {
        assertInvalid(null);
    }

    @Test
    void 빈_응답을_거부한다() {
        assertInvalid(List.of());
    }

    @Test
    void null_항목을_거부한다() {
        assertInvalid(Collections.singletonList(null));
    }

    @Test
    void null_태그명을_거부한다() {
        assertInvalid(List.of(new RawTag(null, "FRAMEWORK")));
    }

    @Test
    void 공백뿐인_태그명을_거부한다() {
        assertInvalid(List.of(new RawTag("   ", "FRAMEWORK")));
    }

    @Test
    void 유니코드_공백뿐인_태그명을_거부한다() {
        assertInvalid(List.of(new RawTag("\u2003\u00A0", "FRAMEWORK")));
    }

    @Test
    void 길이가_100자를_초과한_태그명을_거부한다() {
        assertInvalid(List.of(new RawTag("a".repeat(101), "ETC")));
    }

    @Test
    void 소문자_정규화_후_100자를_초과한_태그명을_거부한다() {
        assertInvalid(List.of(new RawTag("İ".repeat(100), "LANGUAGE")));
    }

    @Test
    void null_카테고리를_거부한다() {
        assertInvalid(List.of(new RawTag("Spring Boot", null)));
    }

    @Test
    void 지원하지_않는_카테고리를_거부한다() {
        assertInvalid(List.of(new RawTag("Spring Boot", "LIBRARY")));
    }

    @Test
    void 하나의_항목이라도_잘못되면_전체_응답을_거부한다() {
        List<RawTag> rawTags = List.of(
            new RawTag("Java", "LANGUAGE"),
            new RawTag(" ", "FRAMEWORK")
        );

        assertInvalid(rawTags);
    }

    private List<RawTag> fiveDistinctTags() {
        return List.of(
            new RawTag("Java", "LANGUAGE"),
            new RawTag("Spring Boot", "FRAMEWORK"),
            new RawTag("PostgreSQL", "DB"),
            new RawTag("Docker", "INFRA"),
            new RawTag("N+1", "ISSUE")
        );
    }

    private void assertInvalid(List<RawTag> rawTags) {
        assertThatThrownBy(() -> validator.validate(rawTags))
            .isInstanceOf(AiGenerationException.class)
            .satisfies(exception ->
                assertThat(((AiGenerationException) exception).errorCode())
                    .isEqualTo(AiGenerationErrorCode.INVALID_TAGGING_RESPONSE)
            );
    }
}
