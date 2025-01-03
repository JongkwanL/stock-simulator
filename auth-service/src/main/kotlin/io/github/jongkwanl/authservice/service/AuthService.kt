package io.github.jongkwanl.authservice.service

import io.github.jongkwanl.authservice.domain.User
import io.github.jongkwanl.authservice.dto.LoginRequest
import io.github.jongkwanl.authservice.dto.RegisterRequest
import io.github.jongkwanl.authservice.dto.AuthResponse
import io.github.jongkwanl.authservice.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun register(request: RegisterRequest): AuthResponse {
        // 사용자명 중복 확인
        val existing = userRepository.findByUsername(request.username)
        require(existing == null) { "Username already exists: ${request.username}" }

        // 비밀번호 암호화
        val encodedPw = passwordEncoder.encode(request.password)

        // 유저 생성
        val user = User(
            username = request.username,
            password = encodedPw,
            roles = setOf("ROLE_USER") // 기본 ROLE_USER 부여
        )
        userRepository.save(user)

        // JWT 토큰 발행
        val token = jwtTokenProvider.createToken(user.username, user.roles.toList())
        return AuthResponse(username = user.username, token = token)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid username or password")

        // 비밀번호 매칭
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid username or password")
        }

        // JWT 토큰 발행
        val token = jwtTokenProvider.createToken(user.username, user.roles.toList())
        return AuthResponse(username = user.username, token = token)
    }
}
