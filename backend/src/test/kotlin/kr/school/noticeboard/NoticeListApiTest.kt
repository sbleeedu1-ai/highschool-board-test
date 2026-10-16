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
class NoticeListApiTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val repository: NoticeRepository,
) {

    @AfterEach
    fun 공지를_비운다() {
        repository.deleteAll()
    }

    /** 시험용 가짜 공지를 저장한다. 작성일시는 항상 직접 정해서 순서가 매번 같게 한다. */
    private fun 공지를_넣는다(제목: String, 작성일시: String): Long =
        repository.save(Notice(title = 제목, body = "가짜 본문", createdAt = Instant.parse(작성일시))).id!!

    @Test
    fun `공지가 하나도 없으면 빈 목록과 0건을 돌려준다`() {
        mockMvc.get("/api/notices").andExpect {
            status { isOk() }
            jsonPath("$.items.length()") { value(0) }
            jsonPath("$.page") { value(1) }
            jsonPath("$.totalPages") { value(0) }
            jsonPath("$.totalElements") { value(0) }
        }
    }

    @Test
    fun `공지는 작성일시 내림차순으로 보이고 제목과 작성일시를 담는다`() {
        val 옛날 = 공지를_넣는다("가장 오래된 공지", "2026-03-02T00:00:00Z")
        val 최근 = 공지를_넣는다("가장 최근 공지", "2026-10-16T05:30:00Z")
        val 중간 = 공지를_넣는다("중간 공지", "2026-05-01T09:00:00Z")

        mockMvc.get("/api/notices").andExpect {
            status { isOk() }
            jsonPath("$.items.length()") { value(3) }
            jsonPath("$.items[0].id") { value(최근) }
            jsonPath("$.items[0].title") { value("가장 최근 공지") }
            jsonPath("$.items[0].createdAt") { value("2026-10-16T05:30:00Z") }
            jsonPath("$.items[1].id") { value(중간) }
            jsonPath("$.items[2].id") { value(옛날) }
        }
    }

    @Test
    fun `작성일시가 같으면 식별자가 큰 공지가 먼저 보인다`() {
        val 먼저 = 공지를_넣는다("먼저 등록", "2026-10-16T05:30:00Z")
        val 나중 = 공지를_넣는다("나중 등록", "2026-10-16T05:30:00Z")

        mockMvc.get("/api/notices").andExpect {
            jsonPath("$.items[0].id") { value(나중) }
            jsonPath("$.items[1].id") { value(먼저) }
        }
    }

    /** 공지 25건: 제목이 "공지 01"(가장 오래됨)부터 "공지 25"(가장 최근)까지 */
    private fun 스물다섯_건을_넣는다() {
        for (n in 1..25) {
            공지를_넣는다("공지 %02d".format(n), "2026-09-%02dT00:00:00Z".format(n))
        }
    }

    @Test
    fun `25건이면 1페이지 10건 2페이지 10건 3페이지 5건으로 나뉜다`() {
        스물다섯_건을_넣는다()

        mockMvc.get("/api/notices?page=1").andExpect {
            jsonPath("$.items.length()") { value(10) }
            jsonPath("$.items[0].title") { value("공지 25") }
            jsonPath("$.items[9].title") { value("공지 16") }
            jsonPath("$.page") { value(1) }
            jsonPath("$.totalPages") { value(3) }
            jsonPath("$.totalElements") { value(25) }
        }
        mockMvc.get("/api/notices?page=2").andExpect {
            jsonPath("$.items.length()") { value(10) }
            jsonPath("$.items[0].title") { value("공지 15") }
            jsonPath("$.items[9].title") { value("공지 06") }
            jsonPath("$.page") { value(2) }
        }
        mockMvc.get("/api/notices?page=3").andExpect {
            jsonPath("$.items.length()") { value(5) }
            jsonPath("$.items[0].title") { value("공지 05") }
            jsonPath("$.items[4].title") { value("공지 01") }
            jsonPath("$.page") { value(3) }
            jsonPath("$.totalPages") { value(3) }
        }
    }

    @Test
    fun `딱 10건이면 전체 페이지는 1이고 2페이지는 비어 있다`() {
        for (n in 1..10) 공지를_넣는다("공지 %02d".format(n), "2026-09-%02dT00:00:00Z".format(n))

        mockMvc.get("/api/notices").andExpect {
            jsonPath("$.items.length()") { value(10) }
            jsonPath("$.totalPages") { value(1) }
        }
        mockMvc.get("/api/notices?page=2").andExpect {
            jsonPath("$.items.length()") { value(0) }
            jsonPath("$.totalPages") { value(1) }
        }
    }

    @Test
    fun `페이지 번호가 없거나 숫자가 아니거나 1보다 작으면 1페이지로 본다`() {
        스물다섯_건을_넣는다()

        for (잘못된_값 in listOf("abc", "0", "-1", "", "1.5")) {
            mockMvc.get("/api/notices") { param("page", 잘못된_값) }.andExpect {
                status { isOk() }
                jsonPath("$.page") { value(1) }
                jsonPath("$.items.length()") { value(10) }
                jsonPath("$.items[0].title") { value("공지 25") }
            }
        }
        mockMvc.get("/api/notices").andExpect {
            jsonPath("$.page") { value(1) }
        }
    }

    @Test
    fun `범위를 넘은 페이지는 빈 목록이고 요청한 번호와 전체 정보는 그대로다`() {
        스물다섯_건을_넣는다()

        mockMvc.get("/api/notices?page=4").andExpect {
            status { isOk() }
            jsonPath("$.items.length()") { value(0) }
            jsonPath("$.page") { value(4) }
            jsonPath("$.totalPages") { value(3) }
            jsonPath("$.totalElements") { value(25) }
        }
    }
}
