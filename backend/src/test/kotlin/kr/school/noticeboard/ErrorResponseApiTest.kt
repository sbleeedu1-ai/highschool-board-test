package kr.school.noticeboard

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ErrorResponseApiTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `알 수 없는 경로는 공통 오류 형식의 404를 돌려준다`() {
        // 공개 목록에 없는 주소는 로그인부터 요구하므로, 로그인한 관리자로 확인한다
        mockMvc.get("/api/no-such-path") {
            with(user("test-admin"))
        }.andExpect {
            status { isNotFound() }
            content { contentTypeCompatibleWith("application/json") }
            jsonPath("$.code") { value("NOT_FOUND") }
            jsonPath("$.message") { isNotEmpty() }
            jsonPath("$.fieldErrors") { isEmpty() }
        }
    }

    @Test
    fun `허용하지 않는 메서드는 500이 아니라 공통 오류 형식의 405를 돌려준다`() {
        // 보안 검사(로그인·CSRF)는 통과시킨 뒤, 메서드가 틀린 경우만 본다
        mockMvc.post("/api/version") {
            with(user("test-admin"))
            with(csrf())
        }.andExpect {
            status { isMethodNotAllowed() }
            content { contentTypeCompatibleWith("application/json") }
            jsonPath("$.code") { value("METHOD_NOT_ALLOWED") }
            jsonPath("$.message") { isNotEmpty() }
            jsonPath("$.fieldErrors") { isEmpty() }
        }
    }
}
