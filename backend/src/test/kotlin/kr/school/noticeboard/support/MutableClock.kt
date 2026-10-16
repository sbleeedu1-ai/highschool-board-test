package kr.school.noticeboard.support

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

/** 테스트에서 손으로 앞으로 돌릴 수 있는 시계 */
class MutableClock(private var now: Instant) : Clock() {
    /** 테스트끼리 시계를 함께 쓰므로, 시계를 돌리는 테스트는 시작할 때 이 값으로 되돌린다 */
    fun reset() {
        now = START
    }

    fun advance(duration: Duration) {
        now = now.plus(duration)
    }

    override fun instant(): Instant = now
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId): Clock = this

    companion object {
        val START: Instant = Instant.parse("2026-10-16T00:00:00Z")
    }
}

/** 앱의 시계를 MutableClock으로 바꿔 끼운다. 테스트 클래스에 @Import(MutableClockConfig::class)로 붙인다 */
@TestConfiguration
class MutableClockConfig {
    @Bean
    @Primary
    fun mutableClock() = MutableClock(MutableClock.START)
}
