package kr.school.noticeboard.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/** 배포된 커밋 번호를 알려 준다. 배포가 실제로 끝났는지 확인할 때 쓴다. */
@RestController
class VersionController(@Value("\${app.version}") private val version: String) {

    @GetMapping("/api/version")
    fun version() = VersionResponse(version)
}

data class VersionResponse(val version: String)
