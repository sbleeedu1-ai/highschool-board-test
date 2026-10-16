package kr.school.noticeboard.auth

import kr.school.noticeboard.support.MutableClock
import kr.school.noticeboard.support.MutableClockConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

/**
 * 운영처럼 비밀 헤더 검사를 켠 상태에서, Pages Functions가 붙이는 헤더를 테스트가 직접 붙여 흉내 낸다(ADR-0003).
 * 로그인 잠금 기록은 서버 메모리에 남으므로 테스트마다 서버를 새로 띄운다.
 */
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = ["app.proxy.check-enabled=true", "app.proxy.secret=test-proxy-secret"],
)
@ActiveProfiles("test")
@Import(MutableClockConfig::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProxySecretAndLockoutApiTest {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var clock: MutableClock

    /** Pages Functions를 거쳐 온 방문자 */
    private fun visitor(ip: String) = ApiClient(port).apply {
        headers["X-Proxy-Secret"] = "test-proxy-secret"
        headers["X-Client-IP"] = ip
        get("/api/auth/me")
    }

    private fun ApiClient.login(password: String) =
        post("/api/auth/login", """{"username":"test-admin","password":"$password"}""").statusCode()

    @Test
    fun `비밀값이 없는 요청은 403이다`() {
        val res = ApiClient(port).get("/api/version")

        assertThat(res.statusCode()).isEqualTo(403)
        assertThat(res.body()).contains("\"code\":\"PROXY_SECRET_INVALID\"")
    }

    @Test
    fun `비밀값이 틀린 요청은 403이다`() {
        val client = ApiClient(port).apply { headers["X-Proxy-Secret"] = "wrong" }

        assertThat(client.get("/api/version").statusCode()).isEqualTo(403)
    }

    @Test
    fun `상태 확인은 비밀값 없이도 열린다`() {
        assertThat(ApiClient(port).get("/actuator/health").statusCode()).isEqualTo(200)
    }

    @Test
    fun `비밀값이 맞으면 평소처럼 응답한다`() {
        assertThat(visitor("203.0.113.1").get("/api/version").statusCode()).isEqualTo(200)
    }

    @Test
    fun `같은 IP에서 5번 연속 실패하면 맞는 비밀번호도 429로 막힌다`() {
        val attacker = visitor("203.0.113.1")
        repeat(5) { assertThat(attacker.login("wrong")).isEqualTo(401) }

        val res = attacker.post("/api/auth/login", LOGIN_OK)

        assertThat(res.statusCode()).isEqualTo(429)
        assertThat(res.body()).contains("\"code\":\"LOGIN_LOCKED\"").contains("잠시 후 다시 시도해 주세요")
    }

    @Test
    fun `4번 실패까지는 막히지 않는다`() {
        val admin = visitor("203.0.113.1")
        repeat(4) { admin.login("wrong") }

        assertThat(admin.login("test-password-1234")).isEqualTo(200)
    }

    @Test
    fun `한 IP가 잠겨도 다른 IP의 관리자는 로그인할 수 있다`() {
        val attacker = visitor("203.0.113.1")
        repeat(5) { attacker.login("wrong") }

        assertThat(visitor("198.51.100.7").login("test-password-1234")).isEqualTo(200)
    }

    @Test
    fun `10분이 지나면 잠금이 풀린다`() {
        val admin = visitor("203.0.113.1")
        repeat(5) { admin.login("wrong") }

        clock.advance(Duration.ofMinutes(9).plusSeconds(59))
        assertThat(admin.login("test-password-1234")).isEqualTo(429)

        clock.advance(Duration.ofSeconds(1))
        assertThat(admin.login("test-password-1234")).isEqualTo(200)
    }

    @Test
    fun `로그인에 성공하면 그 IP의 실패 횟수가 처음부터 다시 센다`() {
        val admin = visitor("203.0.113.1")
        repeat(4) { admin.login("wrong") }
        assertThat(admin.login("test-password-1234")).isEqualTo(200)

        repeat(4) { assertThat(admin.login("wrong")).isEqualTo(401) }
        assertThat(admin.login("test-password-1234")).isEqualTo(200)
    }

    @Test
    fun `비밀값 없이 보낸 가짜 방문자 IP는 믿지 않는다`() {
        // 비밀값이 없으면 요청 자체가 거절되므로, 가짜 IP로 다른 사람을 잠그거나 잠금을 피할 수 없다
        val spoofer = ApiClient(port).apply {
            headers["X-Client-IP"] = "198.51.100.7"
            get("/api/auth/me")
        }

        val res = spoofer.post("/api/auth/login", LOGIN_OK)

        assertThat(res.statusCode()).isEqualTo(403)
        assertThat(res.body()).contains("\"code\":\"PROXY_SECRET_INVALID\"")
    }

    companion object {
        const val LOGIN_OK = """{"username":"test-admin","password":"test-password-1234"}"""
    }
}
