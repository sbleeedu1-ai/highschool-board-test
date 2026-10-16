package kr.school.noticeboard.common

import kr.school.noticeboard.notice.NoticeNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * 예외를 공통 오류 형식(ErrorResponse)으로 바꾼다. 상황별 처리는 이후 티켓에서 덧붙인다.
 * Spring이 이미 아는 오류(없는 주소 404, 허용하지 않는 메서드 405 등)는 상태 코드를 그대로 두고 형식만 바꾼다.
 */
@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val status = HttpStatus.valueOf(statusCode.value())
        val message = if (status == HttpStatus.NOT_FOUND) "요청한 주소를 찾을 수 없습니다" else "요청을 처리할 수 없습니다"
        return ResponseEntity.status(status).headers(headers).body(ErrorResponse(status.name, message))
    }

    /** 없는 공지. 공지 상세·수정·삭제가 함께 쓴다. */
    @ExceptionHandler(NoticeNotFoundException::class)
    fun noticeNotFound(e: NoticeNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse("NOTICE_NOT_FOUND", "공지를 찾을 수 없습니다"))

    /** 예상하지 못한 오류. 원인은 로그에만 남기고 방문자에게는 다시 시도하라고만 알린다. */
    @ExceptionHandler(Exception::class)
    fun unexpected(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("처리하지 못한 오류", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("INTERNAL_ERROR", "잠시 후 다시 시도해 주세요"))
    }
}
