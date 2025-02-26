package com.example.university.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Room Database 객체
 */
@Entity(tableName = "tb_calendar")
data class CalendarDataEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    // 증상 종류 정보
    @ColumnInfo(name = "symptom_kind")
    var symptomKind: String? = "",

    // 메모 기록 정보
    @ColumnInfo(name = "memo")
    var memo: String? = "",

    // 날짜 정보
    @ColumnInfo(name = "date_str")
    var dateStr: String? = "",

    ) : Serializable {
}