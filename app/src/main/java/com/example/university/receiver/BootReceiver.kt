package com.example.university.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.university.database.CalendarDataEntity
import com.example.university.database.MyRoomDatabase
import java.util.*

class BootReceiver: BroadcastReceiver() {

    private lateinit var roomDatabase: MyRoomDatabase

    override fun onReceive(context: Context, intent: Intent) {
        roomDatabase = MyRoomDatabase.getInstance(context)!!

        // 휴대폰이 재부팅 된 경우 모든 알람이 제거되기 때문에 재등록
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val calendarDataEntityList: List<CalendarDataEntity> = roomDatabase.calendarDataDao().getAllData()
            for (calendarDataEntity in calendarDataEntityList) {
                createAlarm(context, calendarDataEntity)
            }
        }
    }

    // 알람 생성
    private fun createAlarm(context: Context, data: CalendarDataEntity) {
        if(!isPostDate(data)) {
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply { putExtra("calendarDataEntity", data) }
        val alarmIntent = PendingIntent.getBroadcast(context, data.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (year, month, day) = data.dateStr!!.split("-").map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.AM_PM, 0)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
        }

        // 선택한 날짜에 알림 예약
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
    }

    // 현재 시간보다 이전 날짜이면 알람 생성하지 않음
    // 이 처리가 없다면, 알람은 즉각적으로 울림
    // 선택한 날짜가 오늘과 같거나 전이라면, return false
    private fun isPostDate(calData: CalendarDataEntity): Boolean {
        val (year, month, day) = calData.dateStr!!.split("-").map { it.toInt() }
        val today = Calendar.getInstance().apply { timeZone = TimeZone.getDefault() }
        return ((year <= today[Calendar.YEAR]) && (month <= (today[Calendar.MONTH] + 1)) && (day < today[Calendar.DAY_OF_MONTH]))
    }
}