package kr.school.noticeboard.notice

/** 요청한 공지가 없을 때 던진다. 공통 오류 형식의 404(NOTICE_NOT_FOUND)로 바뀐다. */
class NoticeNotFoundException : RuntimeException("공지를 찾을 수 없습니다")
