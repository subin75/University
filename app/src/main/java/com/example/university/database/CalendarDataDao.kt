package com.example.university.database

import androidx.room.*

/**
 * Data Access Object (Dao)
 */
@Dao
interface CalendarDataDao {

    // database table 에 삽입 (추가)
    @Insert
    fun insertData(calendarDataEntity: CalendarDataEntity): Long

    // database table 에 기존에 존재하는 데이터를 수정
    @Update
    fun updateData(calendarDataEntity: CalendarDataEntity)

    // database table 에 기존에 존재하는 데이터를 삭제
    @Delete
    fun deleteData(calendarDataEntity: CalendarDataEntity)

    // database table 에 모든 데이터를 삭제 한다.
    @Query("DELETE FROM tb_calendar")
    fun deleteAllData()

    // database table 에 전체 데이터를 가지고 옴. (조회)
    @Query("SELECT * FROM tb_calendar WHERE date_str = :dateStr")
    fun getSelectedReadData(dateStr: String): CalendarDataEntity?

    @Query("SELECT * FROM tb_calendar")
    fun getAllData(): List<CalendarDataEntity>
}