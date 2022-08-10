package com.woowacourse.momo.auth.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import lombok.RequiredArgsConstructor;

import com.woowacourse.momo.auth.domain.Token;
import com.woowacourse.momo.auth.domain.TokenRepository;
import com.woowacourse.momo.auth.support.AuthorizationExtractor;
import com.woowacourse.momo.auth.support.JwtTokenProvider;
import com.woowacourse.momo.globalException.exception.ErrorCode;
import com.woowacourse.momo.globalException.exception.MomoException;

@RequiredArgsConstructor
public class RefreshTokenAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String refreshToken = AuthorizationExtractor.extract(request);
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new MomoException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        Long memberId = jwtTokenProvider.getPayload(refreshToken);
        Token token = tokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MomoException(ErrorCode.AUTH_INVALID_TOKEN));

        if (!token.ieSameRefreshToken(refreshToken)) {
            throw new MomoException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        return true;
    }
}
