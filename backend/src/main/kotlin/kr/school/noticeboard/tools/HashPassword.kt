package kr.school.noticeboard.tools

import org.springframework.security.crypto.factory.PasswordEncoderFactories

/**
 * 관리자 비밀번호의 해시를 만드는 로컬 전용 명령(`./gradlew hashPassword`).
 * 비밀번호를 입력받아 해시만 출력하고, 비밀번호 원문은 어디에도 저장하거나 출력하지 않는다.
 * 출력된 해시를 ADMIN_PASSWORD_HASH 환경변수(로컬은 backend/.env.local)에 넣는다.
 */
fun main() {
    val console = System.console()
    val password = if (console != null) {
        console.readPassword("새 관리자 비밀번호: ")?.let { String(it) }
    } else {
        // Gradle처럼 콘솔이 없는 환경에서는 입력한 글자가 화면에 보일 수 있다
        print("새 관리자 비밀번호(입력이 화면에 보일 수 있음): ")
        readlnOrNull()
    }
    if (password.isNullOrBlank()) {
        System.err.println("비밀번호가 비어 있습니다. 해시를 만들지 않았습니다.")
        return
    }
    println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password))
}
