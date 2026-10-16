package kr.school.noticeboard.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.security.MessageDigest

/**
 * 모든 요청이 Pages Functions를 거쳐 왔는지 비밀값(X-Proxy-Secret)으로 확인한다(ADR-0003).
 * - 비밀값이 없거나 틀리면 403으로 거절한다. Railway 상태 확인(/actuator/health)만 예외다.
 * - 비밀값이 맞을 때만 X-Client-IP(방문자 IP)를 믿고, 로그인 잠금이 쓸 수 있게 요청에 적어 둔다.
 * - 검사를 끈 환경(local·test)에서는 실제 접속 주소를 방문자 IP로 쓴다.
 */
class ProxySecretFilter(
    private val checkEnabled: Boolean,
    secret: String,
    private val reject: (HttpServletResponse) -> Unit,
) : OncePerRequestFilter() {

    private val secretBytes = secret.toByteArray()

    init {
        check(!checkEnabled || secret.isNotBlank()) { "비밀 헤더 검사가 켜져 있는데 PROXY_SECRET 환경변수가 비어 있습니다" }
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (!checkEnabled) {
            request.setAttribute(CLIENT_IP_ATTRIBUTE, request.remoteAddr)
            chain.doFilter(request, response)
            return
        }
        if (isHealthCheck(request)) {
            chain.doFilter(request, response)
            return
        }
        val given = request.getHeader(SECRET_HEADER)
        // 글자를 앞에서부터 비교하다 멈추면 걸린 시간으로 비밀값을 추측할 수 있으므로, 항상 끝까지 비교한다
        if (given == null || !MessageDigest.isEqual(given.toByteArray(), secretBytes)) {
            reject(response)
            return
        }
        request.setAttribute(CLIENT_IP_ATTRIBUTE, request.getHeader(CLIENT_IP_HEADER) ?: request.remoteAddr)
        chain.doFilter(request, response)
    }

    /**
     * 정리하지 않은 원래 주소로 판단한다. `/actuator/health/../api/...` 같은 주소는 이 필터보다 앞에 있는
     * Spring Security 방화벽(StrictHttpFirewall)이 먼저 400으로 거절하므로 여기까지 오지 않는다. 그 방화벽 설정을 느슨하게 바꾸지 않는다.
     */
    private fun isHealthCheck(request: HttpServletRequest): Boolean {
        val path = request.requestURI.removePrefix(request.contextPath)
        return path == "/actuator/health" || path.startsWith("/actuator/health/")
    }

    companion object {
        const val SECRET_HEADER = "X-Proxy-Secret"
        const val CLIENT_IP_HEADER = "X-Client-IP"
        private const val CLIENT_IP_ATTRIBUTE = "noticeboard.clientIp"

        /** 이 필터가 정해 둔 방문자 IP */
        fun clientIp(request: HttpServletRequest): String =
            request.getAttribute(CLIENT_IP_ATTRIBUTE) as? String ?: request.remoteAddr
    }
}
