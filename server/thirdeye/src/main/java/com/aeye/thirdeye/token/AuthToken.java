package com.aeye.thirdeye.token;

import com.aeye.thirdeye.entity.User;
import com.aeye.thirdeye.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
//@ComponentScan
public class AuthToken {
    
    @Getter
    private final String token;
    private final Key key;

    private final UserRepository userRepository;  // 순서 변경 금지

    private static final String AUTHORITIES_KEY = "role";

    AuthToken(String id, Date expiry, Key key, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.key = key;
        this.token = createAuthToken(id, expiry);
    }

    AuthToken(String id, String role, Date expiry, Key key, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.key = key;
        this.token = createAuthToken(id, role, expiry);
    }

    private String createAuthToken(String id, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(String userId, String role, Date expiry) {
        User user = userRepository.findByUserId(userId);
        Long id = user.getId();
        String nickName = user.getNickName();

        Claims claims = Jwts.claims().setSubject(userId); // JWT payload 에 저장되는 정보단위 (sub)
        claims.put("id", id);
        claims.put("nickname", nickName);
        claims.put("user_id", userId); // user_id를 저장(명명방식에 과거 프로젝트 장고 흔적). 정보는 key / value 쌍으로 저장된다.
        claims.put(AUTHORITIES_KEY, role);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // jwt 추가
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Claims getExpiredTokenClaims() {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("JWT 토큰 만료.");
            return e.getClaims();
        }
        return null;
    }
}
