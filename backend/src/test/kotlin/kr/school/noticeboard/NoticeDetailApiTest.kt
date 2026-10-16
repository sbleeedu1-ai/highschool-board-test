package kr.school.noticeboard

import kr.school.noticeboard.notice.Notice
import kr.school.noticeboard.notice.NoticeRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoticeDetailApiTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val repository: NoticeRepository,
) {

    @AfterEach
    fun 공지를_비운다() {
        repository.deleteAll()
    }

    @Test
    fun `수정한 적 없는 공지는 수정일시가 null이다`() {
        val id = repository.save(
            Notice(title = "가짜 제목", body = "첫 줄\n둘째 줄", createdAt = Instant.parse("2026-10-16T05:30:00Z")),
        ).id!!

        mockMvc.get("/api/notices/$id").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
            jsonPath("$.title") { value("가짜 제목") }
            jsonPath("$.body") { value("첫 줄\n둘째 줄") }
            jsonPath("$.createdAt") { value("2026-10-16T05:30:00Z") }
            jsonPath("$.updatedAt") { value(null) }
        }
    }

    @Test
    fun `수정한 공지는 수정일시를 담는다`() {
        val id = repository.save(
            Notice(
                title = "고친 제목",
                body = "고친 본문",
                createdAt = Instant.parse("2026-10-16T05:30:00Z"),
                updatedAt = Instant.parse("2026-10-17T01:00:00Z"),
            ),
        ).id!!

        mockMvc.get("/api/notices/$id").andExpect {
            status { isOk() }
            jsonPath("$.createdAt") { value("2026-10-16T05:30:00Z") }
            jsonPath("$.updatedAt") { value("2026-10-17T01:00:00Z") }
        }
    }

    @Test
    fun `없는 공지는 공통 형식의 404 NOTICE_NOT_FOUND를 돌려준다`() {
        mockMvc.get("/api/notices/999999").andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("NOTICE_NOT_FOUND") }
            jsonPath("$.message") { value("공지를 찾을 수 없습니다") }
        }
    }

    @Test
    fun `숫자가 아니거나 1보다 작은 식별자도 같은 404를 돌려준다`() {
        for (잘못된_값 in listOf("abc", "0", "-1", "99999999999999999999")) {
            mockMvc.get("/api/notices/$잘못된_값").andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("NOTICE_NOT_FOUND") }
                jsonPath("$.message") { value("공지를 찾을 수 없습니다") }
            }
        }
    }
}
