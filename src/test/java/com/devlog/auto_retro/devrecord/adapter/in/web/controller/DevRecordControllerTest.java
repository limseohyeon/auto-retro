package com.devlog.auto_retro.devrecord.adapter.in.web.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devlog.auto_retro.devrecord.adapter.in.web.request.DevRecordCreateRequest;
import com.devlog.auto_retro.devrecord.application.service.DevRecordCreateService;
import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DevRecordController.class)
class DevRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    DevRecordCreateService devRecordCreateService;

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
        //given
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
