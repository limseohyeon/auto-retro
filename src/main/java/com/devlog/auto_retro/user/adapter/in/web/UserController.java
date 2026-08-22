package com.devlog.auto_retro.user.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devlog.auto_retro.common.api.ApiResponse;
import com.devlog.auto_retro.user.application.UserLookupService;

/**
 * 사용자 존재 여부를 확인하는 HTTP 진입점이다.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserLookupService userLookupService;

	public UserController(UserLookupService userLookupService) {
		this.userLookupService = userLookupService;
	}

	@GetMapping("/{userId}")
	public ApiResponse<UserResponse> findUser(@PathVariable long userId) {
		var userInfo = userLookupService.findById(userId);

		return ApiResponse.success(UserResponse.from(userInfo));
	}
}
