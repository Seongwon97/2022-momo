package com.woowacourse.momo.acceptance.auth;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.woowacourse.momo.acceptance.AcceptanceTest;
import com.woowacourse.momo.auth.service.dto.response.LoginResponse;
import com.woowacourse.momo.fixture.MemberFixture;

public class AuthAcceptanceTest extends AcceptanceTest {

    private static final MemberFixture MEMBER_FIXTURE = MemberFixture.MOMO;

    @DisplayName("회원가입을 하다")
    @Test
    void signUp() {
        AuthRestHandler.회원가입을_한다(MEMBER_FIXTURE)
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("로그인을 하다")
    @Test
    void login() {
        AuthRestHandler.회원가입을_한다(MEMBER_FIXTURE);

        AuthRestHandler.로그인을_한다(MEMBER_FIXTURE)
                .statusCode(HttpStatus.OK.value())
                .body("accessToken", Matchers.notNullValue())
                .body("refreshToken", Matchers.notNullValue());
    }

    @DisplayName("리프레시토큰을 통해 새로운 엑세스 토큰을 발급받는다")
    @Test
    void reissueAccessToken() {
        AuthRestHandler.회원가입을_한다(MEMBER_FIXTURE);

        String refreshToken = AuthRestHandler.로그인을_한다(MEMBER_FIXTURE).extract()
                .as(LoginResponse.class).getRefreshToken();

        AuthRestHandler.엑세스토큰을_재발급받는다(refreshToken)
                .statusCode(HttpStatus.OK.value())
                .body("accessToken", Matchers.notNullValue());
    }
}
