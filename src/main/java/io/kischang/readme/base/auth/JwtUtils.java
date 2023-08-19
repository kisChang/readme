package io.kischang.readme.base.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.kischang.readme.app.utils.SpringUtils;
import io.kischang.readme.base.exception.BaseException;
import io.kischang.readme.user.dao.UserAccountDao;
import io.kischang.readme.user.model.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Key;
import java.util.Date;

public class JwtUtils {

    private static final long expire = 2592000; //30天
    private static final Key secret = decodeKey("ULgNsWJ8rPjRtnjzX/Gv2RGS80Ksnm/ZaLpvIL+NrBg=");
    private static final String header = "Authorization";

    // 生成JWT
    public static String generateToken(String username) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + 1000 * expire);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)    // 7天过期
                .signWith(secret)
                .compact();
    }

    private static Key decodeKey(String secret) {
        if (secret == null || "".equals(secret)) {
            return null;
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // 解析JWT
    public static Claims getClaimsByToken(String jwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // 判断JWT是否过期
    public static boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public static String getHeader() {
        return header;
    }

    public static String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                return String.valueOf(authentication.getPrincipal());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static UserAccount getCurrentUser() {
        UserAccountDao accountDao = SpringUtils.getBean(UserAccountDao.class);
        String username = getCurrentUsername();
        if (username != null) {
            UserAccount account = accountDao.findByUsername(username);
            if (account != null) {
                return account;
            }
        }
        throw new BaseException(403, "请先登录");
    }
}