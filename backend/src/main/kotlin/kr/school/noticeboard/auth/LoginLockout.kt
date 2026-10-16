package kr.school.noticeboard.auth

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * 로그인 잠금: 같은 접속 위치(IP)에서 5번 연속 실패하면 그 IP만 10분 동안 로그인 시도를 막는다(ADR-0003).
 * 계정 전체를 막지 않으므로, 다른 곳에서 누가 틀린 비밀번호를 넣어도 관리자는 로그인할 수 있다.
 * 기록은 서버 메모리에만 두므로 재배포하면 사라진다.
 */
@Component
class LoginLockout(private val clock: Clock) {

    /** failures: 잠기기 전까지 쌓인 연속 실패 횟수, lockedUntil: 잠금이 풀리는 시각(잠기지 않았으면 null) */
    private data class Record(val failures: Int, val lockedUntil: Instant?)

    private val records = ConcurrentHashMap<String, Record>()

    fun isLocked(ip: String): Boolean {
        val record = records[ip] ?: return false
        val lockedUntil = record.lockedUntil ?: return false
        if (clock.instant() < lockedUntil) return true
        // 잠금이 풀렸으면 기록을 지우고 처음부터 다시 센다
        records.remove(ip, record)
        return false
    }

    fun recordFailure(ip: String) {
        records.compute(ip) { _, record ->
            val failures = (record?.takeIf { it.lockedUntil == null }?.failures ?: 0) + 1
            if (failures >= MAX_FAILURES) Record(0, clock.instant().plus(LOCK_DURATION)) else Record(failures, null)
        }
    }

    fun recordSuccess(ip: String) {
        records.remove(ip)
    }

    companion object {
        const val MAX_FAILURES = 5
        val LOCK_DURATION: Duration = Duration.ofMinutes(10)
    }
}
