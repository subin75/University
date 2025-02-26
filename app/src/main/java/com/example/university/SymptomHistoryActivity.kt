package com.example.university

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.example.university.database.CalendarDataEntity
import com.example.university.database.MyRoomDatabase
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 증상기록 화면
 */
class SymptomHistoryActivity : AppCompatActivity() {
    private lateinit var roomDatabase: MyRoomDatabase
    private var currentClickedDateStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom_history)

        // init room database
        roomDatabase = MyRoomDatabase.getInstance(applicationContext)!!

        // 입력필드 초기화
        val etSymptomKind: AppCompatEditText = findViewById(R.id.et_symptom_kind)
        val etMemo: AppCompatEditText = findViewById(R.id.et_memo)

        // 입력 데이터 저장 버튼
        val btnSaveData: Button = findViewById(R.id.btn_save_data)
        btnSaveData.setOnClickListener {

            // 날짜 지정을 했는지 검사
            if (currentClickedDateStr.isNullOrEmpty()) {
                Toast.makeText(this@SymptomHistoryActivity, "날짜 지정을 하지 않았습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 입력 값이 비어있는지 검사
            if (etSymptomKind.text.isNullOrBlank() || etMemo.text.isNullOrBlank()) {
                Toast.makeText(this@SymptomHistoryActivity, "입력 값이 비어있습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val checkBeforeCalendarData = roomDatabase.calendarDataDao().getSelectedReadData(currentClickedDateStr!!)
                if (checkBeforeCalendarData == null) {
                    val calendarDataEntity = CalendarDataEntity(symptomKind = etSymptomKind.text.toString(), memo = etMemo.text.toString() , dateStr = currentClickedDateStr)
                    // save room database of wifi list data
                    roomDatabase.calendarDataDao().insertData(calendarDataEntity)
                    runOnUiThread {
                        Toast.makeText(this@SymptomHistoryActivity, "지정된 날짜에 저장되었습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 기존 데이터를 수정해줘야 함
                    checkBeforeCalendarData.symptomKind = etSymptomKind.text.toString()
                    checkBeforeCalendarData.memo = etMemo.text.toString()
                    // update data
                    roomDatabase.calendarDataDao().updateData(checkBeforeCalendarData)
                    runOnUiThread {
                        Toast.makeText(this@SymptomHistoryActivity, "기존기록을 업데이트 하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 캘린더 뷰 초기화
        val calendarView: MaterialCalendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangedListener { widget, calendarDate, selected ->
            // 캘린더 클릭 시 이 곳이 호출됨
            currentClickedDateStr = calendarDate.date.toString()
            Log.d("ClickDateString", currentClickedDateStr!!)
            Log.d("Selected", selected.toString())

            // load click date in data
            CoroutineScope(Dispatchers.IO).launch {
                // 데이터베이스 에서 조회
                val calendarDataEntity = roomDatabase.calendarDataDao().getSelectedReadData(currentClickedDateStr!!)
                runOnUiThread {
                    // 조회완료된 데이터를 입력필드에 표시해줌.
                    if (calendarDataEntity == null) {
                        etSymptomKind.setText("")
                        etMemo.setText("")
                    } else {
                        etSymptomKind.setText(calendarDataEntity.symptomKind.toString())
                        etMemo.setText(calendarDataEntity.memo.toString())
                    }
                }
            }

        }
    }
}