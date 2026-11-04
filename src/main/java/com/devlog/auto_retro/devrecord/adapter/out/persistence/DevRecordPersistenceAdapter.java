package com.devlog.auto_retro.devrecord.adapter.out.persistence;

import com.devlog.auto_retro.devrecord.adapter.out.persistence.entity.DevRecordJpaEntity;
import com.devlog.auto_retro.devrecord.adapter.out.persistence.repository.DevRecordJpaRepository;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordCreateCommand;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordDetail;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSearchCondition;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummary;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummaryPage;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordLoadPort;
import com.devlog.auto_retro.devrecord.application.port.out.DevRecordSavePort;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DevRecordPersistenceAdapter implements DevRecordSavePort, DevRecordLoadPort {

    private final DevRecordJpaRepository devRecordJpaRepository;

    @Override
    public long save(DevRecordCreateCommand command) {
        var entity = DevRecordJpaEntity.create(
            command.userId(),
            command.title(),
            command.workContent(),
            command.problemContent(),
            command.solutionContent(),
            command.learnedContent(),
            command.status(),
            command.recordDate()
        );

        return devRecordJpaRepository
            .save(entity)
            .getId();
    }

    @Override
    public Optional<DevRecordDetail> loadDetail(long recordId) {
        return devRecordJpaRepository.findById(recordId)
            .map(entity -> new DevRecordDetail(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getWorkContent(),
                entity.getProblemContent(),
                entity.getSolutionContent(),
                entity.getLearnedContent(),
                entity.getStatus(),
                entity.getRecordDate()
            ));
    }

    @Override
    public DevRecordSummaryPage loadPage(DevRecordSearchCondition condition) {
        var page = devRecordJpaRepository.findSummaries(
            condition.userId(),
            condition.difficulty(),
            toKeywordPattern(condition.keyword()),
            // 말줄임표 필요 여부를 판단할 수 있도록 미리보기 길이보다 한 글자 더 가져온다.
            DevRecordSummary.PREVIEW_LENGTH + 1,
            PageRequest.of(condition.page(), DevRecordSearchCondition.PAGE_SIZE)
        );

        var content = page.getContent()
            .stream()
            .map(view -> new DevRecordSummary(
                view.getRecordId(),
                view.getTitle(),
                view.getDifficulty(),
                view.getWorkContent(),
                view.getRecordDate()
            ))
            .toList();

        return new DevRecordSummaryPage(
            content,
            page.getNumber(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    private String toKeywordPattern(String keyword) {
        if (keyword == null) {
            return null;
        }
        String escaped = keyword
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
        return "%" + escaped.toLowerCase(Locale.ROOT) + "%";
    }
}
