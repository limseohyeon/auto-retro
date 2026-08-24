package com.devlog.auto_retro.user.adapter.in.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devlog.auto_retro.common.api.ApiResponse;
import com.devlog.auto_retro.user.adapter.in.web.response.UserResponse;
import com.devlog.auto_retro.user.application.service.UserLookupService;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 존재 여부를 확인하는 HTTP 진입점이다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserLookupService userLookupService;

    @GetMapping("/{userNm}")
    public ApiResponse<UserResponse> findUserByUserNm(
        @PathVariable("userNm") String userNm
    ) {
        var userInfo = userLookupService.findByUserNm(userNm);

        return ApiResponse.success(UserResponse.from(userInfo));
    }
}
