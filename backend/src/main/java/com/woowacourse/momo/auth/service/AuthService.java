package com.woowacourse.momo.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.woowacourse.momo.auth.domain.Token;
import com.woowacourse.momo.auth.domain.TokenRepository;
import com.woowacourse.momo.auth.service.dto.request.LoginRequest;
import com.woowacourse.momo.auth.service.dto.request.SignUpRequest;
import com.woowacourse.momo.auth.service.dto.response.AccessTokenResponse;
import com.woowacourse.momo.auth.service.dto.response.LoginResponse;
import com.woowacourse.momo.auth.support.JwtTokenProvider;
import com.woowacourse.momo.auth.support.PasswordEncoder;
import com.woowacourse.momo.globalException.exception.ErrorCode;
import com.woowacourse.momo.globalException.exception.MomoException;
import com.woowacourse.momo.member.domain.Member;
import com.woowacourse.momo.member.domain.MemberRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        String password = passwordEncoder.encrypt(request.getPassword());
        Member member = memberRepository.findByUserIdAndPassword(request.getUserId(), password)
                .orElseThrow(() -> new MomoException(ErrorCode.LOGIN_INVALID_ID_AND_PASSWORD)); // 로그인에 실패했습니다
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        synchronizeRefreshToken(member, refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    private void synchronizeRefreshToken(Member member, String refreshToken) {
        Optional<Token> token = tokenRepository.findByMemberId(member.getId());
        if (token.isPresent()) {
            token.get().updateRefreshToken(refreshToken);
            return;
        }
        tokenRepository.save(new Token(member.getId(), refreshToken));
    }

    @Transactional
    public Long signUp(SignUpRequest request) {
        validateUserId(request.getUserId());
        validateExistUser(request.getUserId());
        String password = passwordEncoder.encrypt(request.getPassword());
        Member member = new Member(request.getUserId(), password, request.getName());
        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }

    private void validateExistUser(String userId) {
        Optional<Member> member = memberRepository.findByUserId(userId);
        if (member.isPresent()) {
            throw new MomoException(ErrorCode.SIGNUP_ALREADY_REGISTER);
        }
    }

    private void validateUserId(String userId) {
        if (userId.contains("@")) {
            throw new MomoException(ErrorCode.SIGNUP_INVALID_ID);
        }
    }

    public AccessTokenResponse reissueAccessToken(Long memberId) {
        boolean isExistMember = memberRepository.existsById(memberId);
        if (!isExistMember) {
            throw new MomoException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        String accessToken = jwtTokenProvider.createAccessToken(memberId);
        return new AccessTokenResponse(accessToken);
    }
}
