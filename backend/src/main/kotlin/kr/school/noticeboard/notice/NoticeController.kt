package kr.school.noticeboard.notice

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

private const val PAGE_SIZE = 10

/** 공지 목록. 작성일시 내림차순(같으면 식별자 내림차순)으로 10건씩 보여 준다. */
@RestController
class NoticeController(private val repository: NoticeRepository) {

    @GetMapping("/api/notices")
    fun list(@RequestParam(required = false) page: String?): NoticeListResponse {
        val pageNumber = parsePage(page)
        val sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        // 화면의 페이지는 1부터, Spring Data의 페이지는 0부터 센다
        val result = repository.findAll(PageRequest.of(pageNumber - 1, PAGE_SIZE, sort))
        return NoticeListResponse(
            items = result.content.map { NoticeSummary(it.id!!, it.title, it.createdAt) },
            page = pageNumber,
            totalPages = result.totalPages,
            totalElements = result.totalElements,
        )
    }

    /** 공지 상세. 식별자가 숫자가 아니거나 1보다 작아도 없는 공지와 똑같이 404로 답한다. */
    @GetMapping("/api/notices/{id}")
    fun detail(@PathVariable id: String): NoticeDetail {
        val noticeId = id.toLongOrNull()?.takeIf { it >= 1 } ?: throw NoticeNotFoundException()
        val notice = repository.findById(noticeId).orElseThrow { NoticeNotFoundException() }
        return NoticeDetail(notice.id!!, notice.title, notice.body, notice.createdAt, notice.updatedAt)
    }

    /** 숫자가 아니거나 1보다 작거나 너무 커서 정수로 못 읽으면 1페이지로 본다. */
    private fun parsePage(raw: String?): Int = raw?.toIntOrNull()?.takeIf { it >= 1 } ?: 1
}

data class NoticeSummary(val id: Long, val title: String, val createdAt: Instant)

data class NoticeDetail(
    val id: Long,
    val title: String,
    val body: String,
    val createdAt: Instant,
    /** 수정한 적이 없으면 null */
    val updatedAt: Instant?,
)

data class NoticeListResponse(
    val items: List<NoticeSummary>,
    val page: Int,
    val totalPages: Int,
    val totalElements: Long,
)
