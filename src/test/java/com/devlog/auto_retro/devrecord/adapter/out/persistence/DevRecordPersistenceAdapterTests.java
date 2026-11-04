package com.devlog.auto_retro.devrecord.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.devlog.auto_retro.devrecord.adapter.out.persistence.entity.DevRecordJpaEntity;
import com.devlog.auto_retro.devrecord.adapter.out.persistence.repository.DevRecordJpaRepository;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSearchCondition;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummary;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummaryPage;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DevRecordPersistenceAdapter.class)
class DevRecordPersistenceAdapterTests {

    @Autowired
    private DevRecordPersistenceAdapter devRecordPersistenceAdapter;

    @Autowired
    private DevRecordJpaRepository devRecordJpaRepository;

    @Autowired
    private JdbcClient jdbcClient;

    private long userId;
    private long otherUserId;

    @BeforeEach
    void setUp() {
        userId = insertUser("list-test-user");
        otherUserId = insertUser("list-test-other-user");
    }

    @Test
    void 요청한_사용자의_기록만_조회한다() {
        // given
        long recordId = save(userId, "내 기록", DevRecordStatus.EASY, LocalDate.of(2026, 10, 1));
        save(otherUserId, "남의 기록", DevRecordStatus.EASY, LocalDate.of(2026, 10, 1));

        // when
        DevRecordSummaryPage page = load(null, null, 0);

        // then
        assertThat(page.content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(recordId);
        assertThat(page.totalElements()).isEqualTo(1);
    }

    @Test
    void 작업일_내림차순_같은_작업일은_기록_ID_내림차순으로_정렬한다() {
        // given
        long older = save(userId, "A", DevRecordStatus.EASY, LocalDate.of(2026, 10, 1));
        long sameDayFirst = save(userId, "B", DevRecordStatus.EASY, LocalDate.of(2026, 10, 2));
        long sameDaySecond = save(userId, "C", DevRecordStatus.EASY, LocalDate.of(2026, 10, 2));
        long newest = save(userId, "D", DevRecordStatus.EASY, LocalDate.of(2026, 10, 3));

        // when
        DevRecordSummaryPage page = load(null, null, 0);

        // then
        assertThat(page.content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(newest, sameDaySecond, sameDayFirst, older);
    }

    @Test
    void 페이지당_10건을_조회하고_전체_개수와_페이지_수를_반환한다() {
        // given
        for (int day = 1; day <= 11; day++) {
            save(userId, "기록 " + day, DevRecordStatus.EASY, LocalDate.of(2026, 10, day));
        }

        // when
        DevRecordSummaryPage first = load(null, null, 0);
        DevRecordSummaryPage second = load(null, null, 1);

        // then
        assertThat(first.content()).hasSize(10);
        assertThat(second.content()).hasSize(1);
        assertThat(second.page()).isEqualTo(1);
        assertThat(second.totalElements()).isEqualTo(11);
        assertThat(second.totalPages()).isEqualTo(2);
    }

    @Test
    void 제목_키워드를_대소문자_구분없이_부분_일치로_검색한다() {
        // given
        long matched = save(userId, "Spring JPA 공부", DevRecordStatus.EASY, LocalDate.of(2026, 10, 1));
        save(userId, "React 공부", DevRecordStatus.EASY, LocalDate.of(2026, 10, 2));

        // when
        DevRecordSummaryPage page = load("jpa", null, 0);

        // then
        assertThat(page.content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(matched);
    }

    @Test
    void 키워드의_퍼센트와_밑줄과_역슬래시는_일반_문자로_검색한다() {
        // given
        long percent = save(userId, "진행률 100% 달성", DevRecordStatus.EASY, LocalDate.of(2026, 10, 1));
        long underscore = save(userId, "snake_case 정리", DevRecordStatus.EASY, LocalDate.of(2026, 10, 2));
        long backslash = save(userId, "C:\\path 정리", DevRecordStatus.EASY, LocalDate.of(2026, 10, 3));
        save(userId, "100 달성", DevRecordStatus.EASY, LocalDate.of(2026, 10, 4));
        save(userId, "snakeXcase 정리", DevRecordStatus.EASY, LocalDate.of(2026, 10, 5));

        // when & then
        assertThat(load("%", null, 0).content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(percent);
        assertThat(load("_", null, 0).content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(underscore);
        assertThat(load("\\", null, 0).content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(backslash);
    }

    @Test
    void 난도로_필터링한다() {
        // given
        long normal = save(userId, "기록", DevRecordStatus.NORMAL, LocalDate.of(2026, 10, 1));
        save(userId, "기록", DevRecordStatus.EASY, LocalDate.of(2026, 10, 2));

        // when
        DevRecordSummaryPage page = load(null, DevRecordStatus.NORMAL, 0);

        // then
        assertThat(page.content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(normal);
    }

    @Test
    void 키워드와_난도를_함께_전달하면_두_조건을_모두_만족하는_기록만_조회한다() {
        // given
        long matched = save(userId, "JPA 공부", DevRecordStatus.NORMAL, LocalDate.of(2026, 10, 1));
        save(userId, "JPA 공부", DevRecordStatus.EASY, LocalDate.of(2026, 10, 2));
        save(userId, "React 공부", DevRecordStatus.NORMAL, LocalDate.of(2026, 10, 3));

        // when
        DevRecordSummaryPage page = load("JPA", DevRecordStatus.NORMAL, 0);

        // then
        assertThat(page.content())
            .extracting(DevRecordSummary::recordId)
            .containsExactly(matched);
        assertThat(page.totalElements()).isEqualTo(1);
    }

    @Test
    void 페이지_범위_초과_시_빈_목록과_전체_페이지_정보를_반환한다() {
        // given
        save(userId, "기록", DevRecordStatus.EASY, LocalDate.of(2026, 10, 1));

        // when
        DevRecordSummaryPage page = load(null, null, 5);

        // then
        assertThat(page.content()).isEmpty();
        assertThat(page.page()).isEqualTo(5);
        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.totalPages()).isEqualTo(1);
    }

    @Test
    void 조회_결과가_없으면_빈_목록과_0을_반환한다() {
        // when
        DevRecordSummaryPage page = load("없는 제목", DevRecordStatus.DIFFICULT, 0);

        // then
        assertThat(page.content()).isEmpty();
        assertThat(page.totalElements()).isZero();
        assertThat(page.totalPages()).isZero();
    }

    @Test
    void 작업_내용은_미리보기_판단에_필요한_151자까지만_가져온다() {
        // given
        save(userId, "기록", DevRecordStatus.EASY, LocalDate.of(2026, 10, 1), "가".repeat(200));

        // when
        DevRecordSummary summary = load(null, null, 0).content().getFirst();

        // then
        assertThat(summary.workContent()).isEqualTo("가".repeat(151));
        assertThat(summary.workPreview()).isEqualTo("가".repeat(150) + "…");
    }

    private DevRecordSummaryPage load(String keyword, DevRecordStatus difficulty, int page) {
        return devRecordPersistenceAdapter.loadPage(
            new DevRecordSearchCondition(userId, keyword, difficulty, page)
        );
    }

    private long save(long ownerId, String title, DevRecordStatus status, LocalDate recordDate) {
        return save(ownerId, title, status, recordDate, "작업 내용");
    }

    private long save(
        long ownerId,
        String title,
        DevRecordStatus status,
        LocalDate recordDate,
        String workContent
    ) {
        return devRecordJpaRepository.save(DevRecordJpaEntity.create(
            ownerId, title, workContent, "", "", "", status, recordDate
        )).getId();
    }

    private long insertUser(String userName) {
        return jdbcClient.sql("INSERT INTO users (user_nm) VALUES (:userName) RETURNING user_id")
            .param("userName", userName)
            .query(Long.class)
            .single();
    }
}
