package com.example.university.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.university.R
import com.example.university.database.CalendarDataEntity
import com.example.university.database.MyRoomDatabase
import com.example.university.databinding.FragmentTalkBinding
import com.example.university.receiver.AlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class TalkFragment : Fragment() {

    private lateinit var binding : FragmentTalkBinding
    private lateinit var roomDatabase: MyRoomDatabase
    private var currentClickedDateStr: String? = null

    private val TAG = TalkFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk, container, false)

        // init room database
        roomDatabase = MyRoomDatabase.getInstance(requireContext())!!

        // 입력 데이터 저장 버튼
        binding.btnSaveData.setOnClickListener {

            // 날짜 지정을 했는지 검사
            if (currentClickedDateStr.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "날짜 지정을 하지 않았습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 입력 값이 비어있는지 검사
            if (binding.etSymptomKind.text.isNullOrBlank() || binding.etMemo.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "입력 값이 비어있습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                var calData = roomDatabase.calendarDataDao().getSelectedReadData(currentClickedDateStr!!)
                if (calData == null) {
                    calData = CalendarDataEntity(
                        symptomKind = binding.etSymptomKind.text.toString(),
                        memo = binding.etMemo.text.toString(),
                        dateStr = currentClickedDateStr
                    )
                    // save room database of wifi list data
                    calData.id = roomDatabase.calendarDataDao().insertData(calData).toInt()

                    // 코루틴에서 메인스레드를 이용하기 위해 withContext 사용
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "지정된 날짜에 저장되었습니다", Toast.LENGTH_SHORT).show()
                    }

                    // 알림 생성
                    createAlarm(calData)

                } else {
                    // 기존 데이터를 수정해줘야 함
                    calData.symptomKind = binding.etSymptomKind.text.toString()
                    calData.memo = binding.etMemo.text.toString()
                    // update data
                    roomDatabase.calendarDataDao().updateData(calData)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "기존기록을 업데이트 하였습니다", Toast.LENGTH_SHORT).show()
                    }

                    // 기존 알림 제거
                    deleteAlarm(calData)

                    // 새로운 알림 생성
                    createAlarm(calData)
                }

                withContext(Dispatchers.Main) {

                    // 등록 완료된 데이터 정보를 표시해줌.
                    with(binding.layoutInfo) {
                        isVisible = true
                        binding.tvSymptomKind.text = (calData.symptomKind ?: "").toString()
                        binding.tvMemo.text = (calData.memo ?: "").toString()
                    }
                    // 입력 필드를 가림.
                    with(binding.layoutInput) {
                        isVisible = false
                        binding.etSymptomKind.setText((calData.symptomKind ?: "").toString())
                        binding.etMemo.setText((calData.memo ?: "").toString())
                    }
                }
            }
        }

        // 증상기록 수정 버튼
        binding.btnEdit.setOnClickListener {
            binding.layoutInfo.isVisible = false
            binding.layoutInput.isVisible = true
        }

        // 증상기록 삭제 버튼
        binding.btnDelete.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val checkBeforeCalendarData = roomDatabase.calendarDataDao().getSelectedReadData(currentClickedDateStr!!) ?: return@launch

                // 증상 기록 삭제
                roomDatabase.calendarDataDao().deleteData(checkBeforeCalendarData)

                // 기존 알림 제거
                deleteAlarm(checkBeforeCalendarData)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "기존기록을 삭제하였습니다", Toast.LENGTH_SHORT).show()

                    // 삭제 완료된 데이터 정보를 가림.
                    with(binding.layoutInfo) {
                        isVisible = false
                        binding.tvSymptomKind.text = ""
                        binding.tvMemo.text = ""
                    }
                    // 입력 필드를 표시.
                    with(binding.layoutInput) {
                        isVisible = true
                        binding.etSymptomKind.setText("")
                        binding.etMemo.setText("")
                    }
                }
            }
        }

        // 캘린더 뷰 초기화
        binding.calendarView.setOnDateChangedListener { widget, calendarDate, selected ->
            // 캘린더 클릭 시 이 곳이 호출됨
            currentClickedDateStr = calendarDate.date.toString()
            binding.tvDate.text = calendarDate.date.toString()

            Log.d("ClickDateString", currentClickedDateStr!!)
            Log.d("Selected", selected.toString())

            // load click date in data
            CoroutineScope(Dispatchers.IO).launch {
                // 데이터베이스 에서 조회
                val calendarDataEntity = roomDatabase.calendarDataDao().getSelectedReadData(currentClickedDateStr!!)
                withContext(Dispatchers.Main) {

                    // 조회된 데이터가 있다면 표시
                    with(binding.layoutInfo) {
                        isVisible = calendarDataEntity != null
                        binding.tvSymptomKind.text = (calendarDataEntity?.symptomKind ?: "").toString()
                        binding.tvMemo.text = (calendarDataEntity?.memo ?: "").toString()
                    }

                    // 조회된 데이터가 없다면 입력필드 표시
                    with(binding.layoutInput) {
                        isVisible = calendarDataEntity == null
                        binding.etSymptomKind.setText((calendarDataEntity?.symptomKind ?: "").toString())
                        binding.etMemo.setText((calendarDataEntity?.memo ?: "").toString())
                    }
                }
            }
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_homeFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_bookmarkFragment)
        }

        return binding.root
    }

    // 알람 생성
    private fun createAlarm(data: CalendarDataEntity) {
        if(!isPostDate(data)) {
            return
        }

        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply { putExtra("calendarDataEntity", data) }
        val alarmIntent = PendingIntent.getBroadcast(requireContext(), data.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmMgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

    // 알림 제거
    private fun deleteAlarm(data: CalendarDataEntity) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(requireContext(), data.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmMgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
    }

    // 현재 시간보다 이전 날짜이면 알람 생성하지 않음
    // 이 처리가 없다면, 알람은 즉각적으로 울림
    // 선택한 날짜가 오늘과 같거나 전이라면, return false
    private fun isPostDate(calData: CalendarDataEntity): Boolean {
        val (year, month, day) = calData.dateStr!!.split("-").map { it.toInt() }
        val today = Calendar.getInstance().apply { timeZone = TimeZone.getDefault() }
        return ((year >= today[Calendar.YEAR]) && (month >= (today[Calendar.MONTH] + 1)) && (day > today[Calendar.DAY_OF_MONTH]))
    }
}