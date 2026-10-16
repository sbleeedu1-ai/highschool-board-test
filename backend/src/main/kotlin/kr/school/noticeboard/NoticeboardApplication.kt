package kr.school.noticeboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NoticeboardApplication

fun main(args: Array<String>) {
	runApplication<NoticeboardApplication>(*args)
}
