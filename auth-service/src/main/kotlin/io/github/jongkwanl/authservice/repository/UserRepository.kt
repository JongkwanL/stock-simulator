package io.github.jongkwanl.authservice.repository

import io.github.jongkwanl.authservice.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}
