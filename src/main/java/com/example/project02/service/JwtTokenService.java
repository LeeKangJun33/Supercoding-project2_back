package com.example.project02.service;

import com.example.project02.entity.User;
import com.example.project02.dto.UserRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final UserService userService;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.accessPlusHour}")
    private long plusHour;

    // 토큰값 생성
    public String create(Map<String, Object> claims){
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());
        LocalDateTime expireAt = LocalDateTime.now().plusHours(plusHour);
        var _expireAt = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setClaims(claims)
                .setExpiration(_expireAt)
                .compact();
    }


    // 생성한 코드 로그인 시 넘겨줌
    public HttpHeaders createToken(UserRequest userRequest) {
        User user = userService.findByEmail(userRequest.getEmail());
        var claims = new HashMap<String, Object>();
        claims.put("user_id", user.getId());
        String token = create(claims);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    public Long decodeToken(String token){
        validation(token);
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("user_id", Long.class);
    }

    // 토큰 검증
    public void validation(String token){
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());
        var parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        try {
            var result = parser.parseClaimsJws(token);
            for (Map.Entry<String, Object> value : result.getBody().entrySet()) {
                log.info("key : {}, value : {}", value.getKey(), value.getValue());
            }
        } catch (Exception e){
            if(e instanceof SignatureException){
                throw new RuntimeException("잘못된 token 값입니다.");
            } else if (e instanceof ExpiredJwtException) {
                throw new RuntimeException("token 시간이 만료되었습니다.");
            } else throw new RuntimeException("알 수 없는 오류가 발생했습니다." + e.getMessage());
        }
    }

}
