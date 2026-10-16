package kr.school.noticeboard.auth

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * 실제로 뜬 서버에 브라우저처럼 요청을 보내는 테스트용 도구.
 * 받은 쿠키를 기억했다가 다음 요청에 붙이고, CSRF 쿠키 값을 헤더에도 붙인다(프론트가 하는 일과 같다).
 */
class ApiClient(private val port: Int) {

    private val http = HttpClient.newHttpClient()
    val cookies = mutableMapOf<String, String>()

    /** 마지막으로 받은 Set-Cookie 줄 전체(속성 확인용) */
    var lastSetCookies: List<String> = emptyList()
        private set

    fun get(path: String) = send("GET", path, null, withCsrf = false)

    fun post(path: String, json: String? = null, withCsrf: Boolean = true) = send("POST", path, json, withCsrf)

    private fun send(method: String, path: String, json: String?, withCsrf: Boolean): HttpResponse<String> {
        val builder = HttpRequest.newBuilder(URI.create("http://localhost:$port$path"))
            .method(method, json?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "application/json")
        if (cookies.isNotEmpty()) builder.header("Cookie", cookies.entries.joinToString("; ") { "${it.key}=${it.value}" })
        if (withCsrf) cookies["XSRF-TOKEN"]?.let { builder.header("X-XSRF-TOKEN", it) }

        val response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString())
        lastSetCookies = response.headers().allValues("Set-Cookie")
        lastSetCookies.forEach { line ->
            val (name, value) = line.substringBefore(";").split("=", limit = 2)
            if (value.isEmpty() || line.contains("Max-Age=0")) cookies.remove(name) else cookies[name] = value
        }
        return response
    }
}
