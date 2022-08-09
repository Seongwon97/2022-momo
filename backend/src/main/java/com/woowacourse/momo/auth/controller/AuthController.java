package com.woowacourse.momo.auth.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.woowacourse.momo.auth.config.AuthenticationPrincipal;
import com.woowacourse.momo.auth.service.AuthService;
import com.woowacourse.momo.auth.service.dto.request.LoginRequest;
import com.woowacourse.momo.auth.service.dto.request.SignUpRequest;
import com.woowacourse.momo.auth.service.dto.response.AccessTokenResponse;
import com.woowacourse.momo.auth.service.dto.response.LoginResponse;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/reissueAccessToken")
    public ResponseEntity<AccessTokenResponse> reissueAccessToken(@AuthenticationPrincipal Long memberId) {
        AccessTokenResponse response = authService.reissueAccessToken(memberId);
        return ResponseEntity.ok(response);
    }
}
