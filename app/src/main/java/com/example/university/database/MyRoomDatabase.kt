package com.example.university.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database 추상 클래스
 */
@Database(entities = [CalendarDataEntity::class], version = 3)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun calendarDataDao(): CalendarDataDao

    companion object {
        private var instance: MyRoomDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : MyRoomDatabase? {
            if (instance == null) {
                synchronized(MyRoomDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyRoomDatabase::class.java,
                        "calendar-database"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                }
            }
            return instance
        }
    }

}