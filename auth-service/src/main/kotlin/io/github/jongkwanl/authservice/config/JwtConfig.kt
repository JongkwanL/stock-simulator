package io.github.jongkwanl.authservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "jwt")
class JwtConfig {
    lateinit var secretKey: String
    var expiration: Long = 3600000  // 1시간(단위: ms)
}
