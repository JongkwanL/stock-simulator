package io.github.jongkwanl.authservice.dto

import jakarta.validation.constraints.NotBlank

/**
 * 회원가입 요청 DTO
 */
data class RegisterRequest(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)

/**
 * 로그인 요청 DTO
 */
data class LoginRequest(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)

/**
 * 회원가입 / 로그인 후 응답 DTO (예: JWT 토큰)
 */
data class AuthResponse(
    val username: String,
    val token: String
)
