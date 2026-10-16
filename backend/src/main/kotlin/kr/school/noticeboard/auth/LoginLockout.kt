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

    /** IP 하나의 상태: 실패를 세는 중이거나, 잠겨 있다 */
    private sealed interface State {
        /** 마지막 실패에서 10분이 지나면 지운다 */
        fun expiresAt(): Instant

        data class Counting(val failures: Int, val lastFailureAt: Instant) : State {
            override fun expiresAt(): Instant = lastFailureAt.plus(WINDOW)
        }

        data class Locked(val until: Instant) : State {
            override fun expiresAt(): Instant = until
        }
    }

    private val states = ConcurrentHashMap<String, State>()

    fun isLocked(ip: String): Boolean = current(ip) is State.Locked

    fun recordFailure(ip: String) {
        val now = clock.instant()
        states.compute(ip) { _, state ->
            when (val live = state?.takeIf { now < it.expiresAt() }) {
                // 잠긴 동안 들어온 실패(동시에 보낸 요청 등)는 잠금을 그대로 둔다
                is State.Locked -> live
                is State.Counting ->
                    if (live.failures + 1 >= MAX_FAILURES) State.Locked(now.plus(LOCK_DURATION))
                    else State.Counting(live.failures + 1, now)
                null -> State.Counting(1, now)
            }
        }
        removeExpiredIfTooMany(now)
    }

    fun recordSuccess(ip: String) {
        states.remove(ip)
    }

    /** 만료된 기록은 지우고, 살아 있는 상태만 돌려준다 */
    private fun current(ip: String): State? {
        val state = states[ip] ?: return null
        if (clock.instant() < state.expiresAt()) return state
        states.remove(ip, state)
        return null
    }

    /** IP를 바꿔 가며 한 번씩 틀리는 요청으로 메모리가 끝없이 늘지 않게, 기록이 많아지면 만료된 것을 치운다 */
    private fun removeExpiredIfTooMany(now: Instant) {
        if (states.size < CLEANUP_THRESHOLD) return
        states.entries.removeIf { now >= it.value.expiresAt() }
    }

    companion object {
        const val MAX_FAILURES = 5
        val LOCK_DURATION: Duration = Duration.ofMinutes(10)

        /** 연속 실패로 보는 시간. 마지막 실패에서 이만큼 지나면 횟수를 처음부터 다시 센다 */
        val WINDOW: Duration = Duration.ofMinutes(10)
        private const val CLEANUP_THRESHOLD = 10_000
    }
}
