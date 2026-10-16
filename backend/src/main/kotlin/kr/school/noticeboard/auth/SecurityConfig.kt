package kr.school.noticeboard.auth

import jakarta.servlet.http.HttpServletResponse
import kr.school.noticeboard.common.ErrorResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.csrf.CsrfException
import tools.jackson.databind.json.JsonMapper

/**
 * 누가 무엇을 할 수 있는지 정한다.
 * - 읽기(GET)는 누구나, 로그인·로그아웃도 누구나 부를 수 있다.
 * - 그 밖의 변경 요청(공지 등록·수정·삭제)은 로그인한 관리자만.
 * - CSRF는 SPA 설정(csrf.spa()): 서버가 XSRF-TOKEN 쿠키를 주고, 프론트가 그 값을 X-XSRF-TOKEN 헤더로 돌려보낸다.
 */
@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, jsonMapper: JsonMapper): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                it.requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                it.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
                it.anyRequest().authenticated()
            }
            .csrf { it.spa() }
            .securityContext { it.securityContextRepository(securityContextRepository()) }
            .logout {
                it.logoutUrl("/api/auth/logout")
                it.logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            }
            .exceptionHandling {
                // 로그인 화면으로 보내지 않고 401을 돌려준다(화면 이동은 프론트가 한다)
                it.authenticationEntryPoint { _, response, _ ->
                    writeError(response, jsonMapper, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다")
                }
                it.accessDeniedHandler { _, response, e ->
                    val code = if (e is CsrfException) "CSRF_INVALID" else "FORBIDDEN"
                    writeError(response, jsonMapper, HttpStatus.FORBIDDEN, code, "다시 시도해 주세요")
                }
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
        return http.build()
    }

    /** 로그인 상태를 서버 메모리 세션에 둔다 */
    @Bean
    fun securityContextRepository(): SecurityContextRepository = HttpSessionSecurityContextRepository()

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    /** 관리자는 한 명뿐이고, 계정은 DB가 아니라 환경변수에서 받는다(ADR-0002) */
    @Bean
    fun userDetailsService(
        @Value("\${app.admin.username}") username: String,
        @Value("\${app.admin.password-hash}") passwordHash: String,
    ): UserDetailsService = InMemoryUserDetailsManager(
        User.withUsername(username).password(passwordHash).roles("ADMIN").build(),
    )

    @Bean
    fun authenticationManager(userDetailsService: UserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationManager {
        val provider = DaoAuthenticationProvider(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(provider)
    }

    private fun writeError(
        response: HttpServletResponse,
        jsonMapper: JsonMapper,
        status: HttpStatus,
        code: String,
        message: String,
    ) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        jsonMapper.writeValue(response.outputStream, ErrorResponse(code, message))
    }
}
