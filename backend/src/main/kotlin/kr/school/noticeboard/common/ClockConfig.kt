package kr.school.noticeboard.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * 현재 시각은 모두 이 시계 하나에서 읽는다(작성일시·수정일시·로그인 잠금 만료).
 * 테스트에서는 이 빈을 고정된 시계로 바꿔 끼워 결과가 매번 같게 만든다.
 */
@Configuration
class ClockConfig {

    @Bean
    fun clock(): Clock = Clock.systemUTC()
}
