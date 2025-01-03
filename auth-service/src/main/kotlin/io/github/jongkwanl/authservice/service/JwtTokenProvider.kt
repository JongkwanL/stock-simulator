package io.github.jongkwanl.authservice.service

import io.github.jongkwanl.authservice.config.JwtConfig
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtTokenProvider(
    private val jwtConfig: JwtConfig
) {
    // secretKey를 ByteArray -> Key 객체로 변환
    private val key = Keys.hmacShaKeyFor(jwtConfig.secretKey.toByteArray())

    /**
     * username, roles 등을 포함하여 JWT 생성
     */
    fun createToken(username: String, roles: List<String>): String {
        val claims = Jwts.claims().setSubject(username)
        claims["roles"] = roles

        val now = Date()
        val expire = Date(now.time + jwtConfig.expiration)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expire)
            .signWith(key)
            .compact()
    }

    /**
     * 토큰 유효성 검사
     */
    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * username, roles를 이용해 Spring Security Authentication 객체 생성
     */
    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
        val username = claims.subject
        val roles = claims["roles"] as? List<*> ?: emptyList<Any>()
        val authorities = roles.map { SimpleGrantedAuthority(it.toString()) }

        // 예: 간단히 UsernamePasswordAuthenticationToken 사용
        return UsernamePasswordAuthenticationToken(username, "", authorities)
    }

    fun getUsername(token: String): String {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject
    }
}
