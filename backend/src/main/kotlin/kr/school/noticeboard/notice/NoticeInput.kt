package kr.school.noticeboard.notice

import kr.school.noticeboard.common.FieldError
import kr.school.noticeboard.common.InvalidInputException

/** 등록·수정 화면이 보내는 내용 */
data class NoticeRequest(val title: String? = null, val body: String? = null)

/** 검사를 통과한 제목과 본문(앞뒤 공백을 지운 값) */
data class NoticeInput(val title: String, val body: String) {

    companion object {
        const val TITLE_MAX = 100
        const val BODY_MAX = 5_000

        /**
         * 앞뒤 공백을 지운 뒤 제목 1-100자, 본문 1-5,000자인지 본다(공백만 있으면 빈 것으로 본다).
         * 글자 수는 화면(JavaScript)·테스트 DB(H2)와 같은 UTF-16 기준이라 이모지는 2자로 센다. PostgreSQL보다 엄격하므로 넘칠 일은 없다.
         * 화면도 같은 검사를 하지만 최종 판정은 여기서 한다. 틀린 칸이 있으면 칸별 안내를 모아 400으로 돌려준다.
         */
        fun from(request: NoticeRequest): NoticeInput {
            val title = request.title.orEmpty().trim(::isTrimmable)
            val body = request.body.orEmpty().trim(::isTrimmable)
            val errors = buildList {
                when {
                    title.isEmpty() -> add(FieldError("title", "제목을 입력해 주세요"))
                    title.length > TITLE_MAX -> add(FieldError("title", "제목은 ${"%,d".format(TITLE_MAX)}자까지 쓸 수 있습니다"))
                }
                when {
                    body.isEmpty() -> add(FieldError("body", "본문을 입력해 주세요"))
                    body.length > BODY_MAX -> add(FieldError("body", "본문은 ${"%,d".format(BODY_MAX)}자까지 쓸 수 있습니다"))
                }
            }
            if (errors.isNotEmpty()) throw InvalidInputException(errors)
            return NoticeInput(title, body)
        }

        /**
         * 화면(JavaScript의 trim)과 같은 기준으로 지울 공백인지 본다.
         * Kotlin 기본 trim은 U+001C-001F를 지우고 BOM(U+FEFF)은 남겨서, 화면과 서버의 판정이 어긋난다.
         */
        private fun isTrimmable(c: Char): Boolean = (c.isWhitespace() && c !in '\u001C'..'\u001F') || c == '\uFEFF'
    }
}
