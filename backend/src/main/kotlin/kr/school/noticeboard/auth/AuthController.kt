package kr.school.noticeboard.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.school.noticeboard.common.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
) {

    /**
     * 로그인 상태를 알려 준다. 화면이 [새 공지]·로그아웃 버튼을 보일지 정할 때 쓴다.
     * CSRF 토큰을 한 번 읽어 XSRF-TOKEN 쿠키가 내려가게 한다(첫 화면에서 이 API를 부르면 이후 변경 요청에 쓸 수 있다).
     */
    @GetMapping("/me")
    fun me(principal: Principal?, csrfToken: CsrfToken): MeResponse {
        csrfToken.token
        return MeResponse(authenticated = principal != null, username = principal?.name)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: LoginRequest,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Any> {
        val authentication = try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(body.username, body.password),
            )
        } catch (e: AuthenticationException) {
            // 아이디와 비밀번호 중 어느 쪽이 틀렸는지 알려 주지 않는다
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse("LOGIN_FAILED", "아이디 또는 비밀번호가 올바르지 않습니다"))
        }

        // 로그인 전 세션을 이어 쓰지 않도록 세션을 새로 만든다(세션 고정 공격 방지)
        request.getSession(false)?.invalidate()
        request.getSession(true)

        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        securityContextRepository.saveContext(context, request, response)

        return ResponseEntity.ok(MeResponse(authenticated = true, username = authentication.name))
    }
}

data class LoginRequest(val username: String = "", val password: String = "")

data class MeResponse(val authenticated: Boolean, val username: String?)
