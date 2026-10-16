package kr.school.noticeboard.common

/** 모든 API 오류가 쓰는 공통 응답 형식. fieldErrors는 입력 오류일 때만 채운다. */
data class ErrorResponse(
    val code: String,
    val message: String,
    val fieldErrors: List<FieldError> = emptyList(),
)

/** 어느 칸(field)이 왜(message) 잘못되었는지 */
data class FieldError(val field: String, val message: String)

/** 입력 오류. 공통 오류 형식의 400과 칸별 안내(fieldErrors)로 바뀐다 */
class InvalidInputException(val fieldErrors: List<FieldError>) : RuntimeException("입력 내용을 확인해 주세요")
