package kr.school.noticeboard

import kr.school.noticeboard.notice.NoticeRepository
import kr.school.noticeboard.support.MutableClockConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(MutableClockConfig::class)
class NoticeCreateApiTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val repository: NoticeRepository,
    @Autowired val jsonMapper: JsonMapper,
) {

    @AfterEach
    fun 공지를_비운다() {
        repository.deleteAll()
    }

    /** 로그인한 관리자가 CSRF 토큰을 붙여 등록한다 */
    private fun create(title: String, body: String): ResultActionsDsl =
        mockMvc.post("/api/notices") {
            with(user("test-admin"))
            with(csrf())
            contentType = MediaType.APPLICATION_JSON
            content = jsonMapper.writeValueAsString(mapOf("title" to title, "body" to body))
        }

    @Test
    fun `관리자가 등록하면 201과 앞뒤 공백을 지운 공지를 돌려주고 작성일시는 시계 값이다`() {
        val result = create("  [예시] 가짜 공지  ", "\n  첫 줄\n둘째 줄  \n").andExpect {
            status { isCreated() }
            jsonPath("$.title") { value("[예시] 가짜 공지") }
            jsonPath("$.body") { value("첫 줄\n둘째 줄") }
            // MutableClockConfig의 시작 시각
            jsonPath("$.createdAt") { value("2026-10-16T00:00:00Z") }
            jsonPath("$.updatedAt") { value(null) }
        }.andReturn()

        val id = jsonMapper.readTree(result.response.contentAsString).get("id").asLong()
        mockMvc.get("/api/notices/$id").andExpect {
            status { isOk() }
            jsonPath("$.title") { value("[예시] 가짜 공지") }
        }
    }

    @Test
    fun `제목 100자는 통과하고 101자는 칸별 안내와 함께 400이다`() {
        create("가".repeat(100), "본문").andExpect { status { isCreated() } }

        create("가".repeat(101), "본문").andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_INPUT") }
            jsonPath("$.fieldErrors.length()") { value(1) }
            jsonPath("$.fieldErrors[0].field") { value("title") }
            jsonPath("$.fieldErrors[0].message") { value("제목은 100자까지 쓸 수 있습니다") }
        }
    }

    @Test
    fun `본문 5000자는 통과하고 5001자는 400이다`() {
        create("제목", "가".repeat(5_000)).andExpect { status { isCreated() } }

        create("제목", "가".repeat(5_001)).andExpect {
            status { isBadRequest() }
            jsonPath("$.fieldErrors[0].field") { value("body") }
            jsonPath("$.fieldErrors[0].message") { value("본문은 5,000자까지 쓸 수 있습니다") }
        }
    }

    @Test
    fun `앞뒤 공백은 글자 수에 넣지 않는다`() {
        create("  " + "가".repeat(100) + "  ", "본문").andExpect { status { isCreated() } }
    }

    @Test
    fun `공백만 있는 제목과 본문은 두 칸 모두 안내한다`() {
        create("   ", " \n\t ").andExpect {
            status { isBadRequest() }
            jsonPath("$.fieldErrors[0].field") { value("title") }
            jsonPath("$.fieldErrors[0].message") { value("제목을 입력해 주세요") }
            jsonPath("$.fieldErrors[1].field") { value("body") }
            jsonPath("$.fieldErrors[1].message") { value("본문을 입력해 주세요") }
        }
        assertThat(repository.count()).isZero()
    }

    @Test
    fun `로그인하지 않으면 401이고 공지가 생기지 않는다`() {
        mockMvc.post("/api/notices") {
            with(csrf())
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"제목","body":"본문"}"""
        }.andExpect { status { isUnauthorized() } }
        assertThat(repository.count()).isZero()
    }

    @Test
    fun `CSRF 토큰이 없으면 403이고 공지가 생기지 않는다`() {
        mockMvc.post("/api/notices") {
            with(user("test-admin"))
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"제목","body":"본문"}"""
        }.andExpect {
            status { isForbidden() }
            jsonPath("$.code") { value("CSRF_INVALID") }
        }
        assertThat(repository.count()).isZero()
    }
}
