package kr.school.noticeboard

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VersionApiTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `커밋 번호가 없으면 버전은 local이다`() {
        mockMvc.get("/api/version").andExpect {
            status { isOk() }
            jsonPath("$.version") { value("local") }
        }
    }
}
