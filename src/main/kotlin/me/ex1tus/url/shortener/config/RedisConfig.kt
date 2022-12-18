package me.ex1tus.url.shortener.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import redis.embedded.RedisServer

private val log = KotlinLogging.logger { }

@Configuration
class RedisConfig(
    @Value("\${redis.port}") private val redisPort: Int
) {

    @Bean
    fun jedisConnectionFactory() = JedisConnectionFactory(
        RedisStandaloneConfiguration().apply {
            port = redisPort
        }
    )

    @Bean
    fun redisTemplate() = RedisTemplate<String, String>().apply {
        setConnectionFactory(jedisConnectionFactory())
    }

    @Bean
    @ConditionalOnProperty("redis.start-test-instance")
    fun embeddedRedisServer() = RedisServer(redisPort)
        .apply { start() }
        .also { log.info { "Started embedded Redis server on port=$redisPort" } }
}
