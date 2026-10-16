package kr.school.noticeboard

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(properties = ["RAILWAY_GIT_COMMIT_SHA=abc1234"])
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VersionApiWithCommitTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `Railway 커밋 번호가 있으면 그 번호를 돌려준다`() {
        mockMvc.get("/api/version").andExpect {
            status { isOk() }
            jsonPath("$.version") { value("abc1234") }
        }
    }
}
