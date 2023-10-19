package com.example.project02.config;

import com.example.project02.dto.AuthInfo;
import com.example.project02.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberAuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenService jwtTokenService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AuthInfo.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader("Authorization");
        if (authorization == null) {
            throw new RuntimeException("UnauthorizedException");
        }
        String token = authorization.substring(7);
        Long userId = jwtTokenService.decodeToken(token);
        if (userId == null) {
            throw new RuntimeException("UnauthorizedException");
        }
        return AuthInfo.of(userId);
    }
}
