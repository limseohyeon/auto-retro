package com.devlog.auto_retro.devrecord.adapter.out.persistence.repository;

import com.devlog.auto_retro.devrecord.adapter.out.persistence.entity.DevRecordJpaEntity;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DevRecordJpaRepository extends JpaRepository<DevRecordJpaEntity, Long> {

    @Query(
        value = """
            select r.id as recordId,
                   r.title as title,
                   r.status as difficulty,
                   substring(r.workContent, 1, :workContentLength) as workContent,
                   r.recordDate as recordDate
            from DevRecordJpaEntity r
            where r.userId = :userId
              and (:difficulty is null or r.status = :difficulty)
              and (:keywordPattern is null or lower(r.title) like :keywordPattern escape '\\')
            order by r.recordDate desc, r.id desc
            """,
        countQuery = """
            select count(r)
            from DevRecordJpaEntity r
            where r.userId = :userId
              and (:difficulty is null or r.status = :difficulty)
              and (:keywordPattern is null or lower(r.title) like :keywordPattern escape '\\')
            """
    )
    Page<DevRecordSummaryView> findSummaries(
        @Param("userId") long userId,
        @Param("difficulty") DevRecordStatus difficulty,
        @Param("keywordPattern") String keywordPattern,
        @Param("workContentLength") int workContentLength,
        Pageable pageable
    );
}
