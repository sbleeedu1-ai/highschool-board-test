package kr.school.noticeboard.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class AuthApiTest {

    @LocalServerPort
    var port: Int = 0

    lateinit var client: ApiClient

    @BeforeEach
    fun setUp() {
        client = ApiClient(port)
    }

    @Test
    fun `로그인하지 않으면 me는 로그인 안 됨을 알려 주고 CSRF 쿠키를 준다`() {
        val res = client.get("/api/auth/me")

        assertThat(res.statusCode()).isEqualTo(200)
        assertThat(res.body()).contains("\"authenticated\":false")
        assertThat(client.cookies).containsKey("XSRF-TOKEN")
    }

    @Test
    fun `맞는 아이디와 비밀번호로 로그인하면 안전한 세션 쿠키를 받고 me가 로그인 상태가 된다`() {
        client.get("/api/auth/me")

        val res = client.post("/api/auth/login", LOGIN_OK)

        assertThat(res.statusCode()).isEqualTo(200)
        val sessionCookie = client.lastSetCookies.single { it.startsWith("JSESSIONID=") }
        assertThat(sessionCookie).contains("HttpOnly").contains("Secure").contains("SameSite=Lax")
        assertThat(client.get("/api/auth/me").body())
            .contains("\"authenticated\":true")
            .contains("\"username\":\"test-admin\"")
    }

    @Test
    fun `비밀번호가 틀리면 401과 하나뿐인 실패 문구를 돌려준다`() {
        client.get("/api/auth/me")

        val res = client.post("/api/auth/login", """{"username":"test-admin","password":"wrong"}""")

        assertThat(res.statusCode()).isEqualTo(401)
        assertThat(res.body()).contains("아이디 또는 비밀번호가 올바르지 않습니다")
        assertThat(client.get("/api/auth/me").body()).contains("\"authenticated\":false")
    }

    @Test
    fun `없는 아이디도 비밀번호가 틀린 경우와 같은 문구를 돌려준다`() {
        client.get("/api/auth/me")

        val res = client.post("/api/auth/login", """{"username":"nobody","password":"test-password-1234"}""")

        assertThat(res.statusCode()).isEqualTo(401)
        assertThat(res.body()).contains("아이디 또는 비밀번호가 올바르지 않습니다")
    }

    @Test
    fun `로그아웃하면 로그인 상태가 풀린다`() {
        client.get("/api/auth/me")
        client.post("/api/auth/login", LOGIN_OK)

        val res = client.post("/api/auth/logout")

        assertThat(res.statusCode()).isEqualTo(204)
        assertThat(client.get("/api/auth/me").body()).contains("\"authenticated\":false")
    }

    @Test
    fun `CSRF 토큰 없이 보낸 변경 요청은 403이다`() {
        client.get("/api/auth/me")

        val res = client.post("/api/auth/login", LOGIN_OK, withCsrf = false)

        assertThat(res.statusCode()).isEqualTo(403)
        assertThat(res.body()).contains("\"code\":\"CSRF_INVALID\"").contains("다시 시도해 주세요")
    }

    @Test
    fun `로그인하지 않고 보낸 변경 요청은 401이다`() {
        client.get("/api/auth/me")

        val res = client.post("/api/notices", """{"title":"t","body":"b"}""")

        assertThat(res.statusCode()).isEqualTo(401)
        assertThat(res.body()).contains("\"code\":\"UNAUTHORIZED\"")
    }

    companion object {
        const val LOGIN_OK = """{"username":"test-admin","password":"test-password-1234"}"""
    }
}
