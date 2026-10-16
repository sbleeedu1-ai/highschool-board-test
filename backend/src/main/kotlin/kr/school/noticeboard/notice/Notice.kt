package kr.school.noticeboard.notice

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.Instant

/** 공지 한 건. 시각은 UTC 시점(Instant)으로 다룬다. */
@Entity
class Notice(
    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = false, length = 5000)
    var body: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    /** 수정한 적이 없으면 null */
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
