package com.devlog.auto_retro.devrecord.adapter.in.web.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devlog.auto_retro.common.error.BusinessException;
import com.devlog.auto_retro.devrecord.adapter.in.web.request.DevRecordCreateRequest;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordDetail;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSearchCondition;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummary;
import com.devlog.auto_retro.devrecord.application.dto.DevRecordSummaryPage;
import com.devlog.auto_retro.devrecord.application.service.DevRecordCreateService;
import com.devlog.auto_retro.devrecord.application.service.DevRecordDetailService;
import com.devlog.auto_retro.devrecord.application.service.DevRecordListService;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import com.devlog.auto_retro.devrecord.error.DevRecordErrorCode;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DevRecordController.class)
class DevRecordControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    DevRecordCreateService devRecordCreateService;

    @MockitoBean
    DevRecordDetailService devRecordDetailService;

    @MockitoBean
    DevRecordListService devRecordListService;

    @Nested
    @DisplayName("개발 기록 생성")
    class CreateDevRecord{

        @Test
        void 개발_기록_생성한다() throws Exception {

            // given
            DevRecordCreateRequest request = new DevRecordCreateRequest(
                1l,
                "테스트용 제목",
                "작업 내용",
                "작업 중 문제 기록",
                "문제 해결 내용",
                "배운 점",
                DevRecordStatus.EASY,
                LocalDate.of(2026, 9, 24)
            );
            given(devRecordCreateService.create(request.toCommand())).willReturn(1l);

            // when & then
            mockMvc.perform(post("/api/dev-records")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/dev-records/1"))
                .andExpect(jsonPath("$.data.recordId").value("1"));
        }

        @Test
        void 제목이_비어있으면_개발_기록_생성에_실패한다() throws Exception{

            // given
            DevRecordCreateRequest request = new DevRecordCreateRequest(
                1l,
                "",
                "작업 내용",
                "작업 중 문제 기록",
                "문제 해결 내용",
                "배운 점",
                DevRecordStatus.EASY,
                LocalDate.of(2026, 9, 24)
            );
            //when & then
            mockMvc.perform(post("/api/dev-records")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verifyNoInteractions(devRecordCreateService);
        }
    }

    @Nested
    @DisplayName("개발 기록 상세 조회")
    class GetDetailDevRecord{

        long recordId = 1L;
        long userId = 1L;

        @Test
        void 개발_기록_상세조회에_성공한다() throws Exception{

            //given
            DevRecordDetail devRecordDetail = new DevRecordDetail(
                recordId,
                userId,
                "테스트용 제목",
                "작업 내용",
                "작업 중 문제 기록",
                "문제 해결 내용",
                "배운 점",
                DevRecordStatus.EASY,
                LocalDate.of(2026, 10, 13)
            );
            given(devRecordDetailService.getDetail(recordId, userId)).willReturn(devRecordDetail);

            //when & then
            mockMvc.perform(get("/api/dev-records/{recordId}", recordId)
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("테스트용 제목"));
        }

        @Test
        void 존재하지_않는_기록의_상세_조회시_404를_반환한다() throws Exception{

            // given
            given(devRecordDetailService.getDetail(recordId, userId))
                .willThrow(new BusinessException(DevRecordErrorCode.RECORD_NOT_FOUND));

            //when & then
            mockMvc.perform(get("/api/dev-records/{recordId}", recordId)
                    .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RECORD-001"))
                .andExpect(jsonPath("$.message").value("개발 기록을 찾을 수 없습니다."));
        }

        @Test
        void userId가_없으면_상세_조회시_400을_반환한다() throws Exception{

            //when & then
            mockMvc.perform(get("/api/dev-records/{recordId}", recordId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON-001"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("userId"));

            verifyNoInteractions(devRecordDetailService);
        }
    }

    @Nested
    @DisplayName("개발 기록 목록 조회")
    class GetDevRecords{

        long userId = 1L;

        @Test
        void 개발_기록_목록_조회에_성공한다() throws Exception{

            // given
            DevRecordSearchCondition condition =
                new DevRecordSearchCondition(userId, "JPA", DevRecordStatus.NORMAL, 1);
            DevRecordSummary summary = new DevRecordSummary(
                15L,
                "JPA 공부",
                DevRecordStatus.NORMAL,
                "작업 내용",
                LocalDate.of(2025, 3, 5)
            );
            given(devRecordListService.getList(condition))
                .willReturn(new DevRecordSummaryPage(List.of(summary), 1, 11L, 2));

            // when & then
            mockMvc.perform(get("/api/dev-records")
                    .param("userId", String.valueOf(userId))
                    .param("page", "1")
                    .param("keyword", "JPA")
                    .param("difficulty", "NORMAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].recordId").value("15"))
                .andExpect(jsonPath("$.data.content[0].title").value("JPA 공부"))
                .andExpect(jsonPath("$.data.content[0].difficulty").value("NORMAL"))
                .andExpect(jsonPath("$.data.content[0].workPreview").value("작업 내용"))
                .andExpect(jsonPath("$.data.content[0].tags").isArray())
                .andExpect(jsonPath("$.data.content[0].tags").isEmpty())
                .andExpect(jsonPath("$.data.content[0].recordDate").value("2025-03-05"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(11))
                .andExpect(jsonPath("$.data.totalPages").value(2));
        }

        @Test
        void page를_생략하면_0페이지를_조회한다() throws Exception{

            // given
            DevRecordSearchCondition condition = new DevRecordSearchCondition(userId, null, null, 0);
            given(devRecordListService.getList(condition))
                .willReturn(new DevRecordSummaryPage(List.of(), 0, 0L, 0));

            // when & then
            mockMvc.perform(get("/api/dev-records")
                    .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));

            verify(devRecordListService).getList(condition);
        }

        @Test
        void 조회_결과가_없으면_200과_빈_목록을_반환한다() throws Exception{

            // given
            given(devRecordListService.getList(new DevRecordSearchCondition(userId, null, null, 0)))
                .willReturn(new DevRecordSummaryPage(List.of(), 0, 0L, 0));

            // when & then
            mockMvc.perform(get("/api/dev-records")
                    .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0));
        }

        @Test
        void userId가_없으면_400을_반환한다() throws Exception{

            // when & then
            mockMvc.perform(get("/api/dev-records"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON-001"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("userId"));

            verifyNoInteractions(devRecordListService);
        }

        @ParameterizedTest(name = "{0}={1}")
        @CsvSource({
            "userId, abc",
            "page, -1",
            "page, abc",
            "difficulty, HARD"
        })
        void 잘못된_요청값이면_400을_반환한다(String name, String value) throws Exception{

            // given
            var request = get("/api/dev-records").param(name, value);
            if (!name.equals("userId")) {
                request.param("userId", String.valueOf(userId));
            }

            // when & then
            mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON-001"))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(name));

            verifyNoInteractions(devRecordListService);
        }

        @Test
        void page가_상한값이면_데이터가_없어도_200과_빈_목록을_반환한다() throws Exception{

            // given
            int maxPage = DevRecordSearchCondition.MAX_PAGE;
            DevRecordSearchCondition condition = new DevRecordSearchCondition(userId, null, null, maxPage);
            given(devRecordListService.getList(condition))
                .willReturn(new DevRecordSummaryPage(List.of(), maxPage, 1L, 1));

            // when & then
            mockMvc.perform(get("/api/dev-records")
                    .param("userId", String.valueOf(userId))
                    .param("page", String.valueOf(maxPage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.page").value(maxPage))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));

            verify(devRecordListService).getList(condition);
        }

        @Test
        void page가_상한값을_넘으면_400을_반환한다() throws Exception{

            // when & then
            mockMvc.perform(get("/api/dev-records")
                    .param("userId", String.valueOf(userId))
                    .param("page", String.valueOf(DevRecordSearchCondition.MAX_PAGE + 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON-001"))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("page"));

            verifyNoInteractions(devRecordListService);
        }
    }
}
