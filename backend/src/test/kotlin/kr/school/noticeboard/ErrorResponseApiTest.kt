package kr.school.noticeboard

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
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
        mockMvc.get("/api/no-such-path").andExpect {
            status { isNotFound() }
            content { contentTypeCompatibleWith("application/json") }
            jsonPath("$.code") { value("NOT_FOUND") }
            jsonPath("$.message") { isNotEmpty() }
            jsonPath("$.fieldErrors") { isEmpty() }
        }
    }

    @Test
    fun `허용하지 않는 메서드는 500이 아니라 공통 오류 형식의 405를 돌려준다`() {
        mockMvc.post("/api/version").andExpect {
            status { isMethodNotAllowed() }
            content { contentTypeCompatibleWith("application/json") }
            jsonPath("$.code") { value("METHOD_NOT_ALLOWED") }
            jsonPath("$.message") { isNotEmpty() }
            jsonPath("$.fieldErrors") { isEmpty() }
        }
    }
}
