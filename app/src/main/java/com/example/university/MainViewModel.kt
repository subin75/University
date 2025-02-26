package com.example.university

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.university.model.HospitalItem
import com.example.university.network.ServerRepository
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.await
import java.net.ProtocolException
import java.net.SocketTimeoutException
import kotlin.reflect.typeOf

// 구조를 다소 변경했습니다
// 기존 구조에서 Runnable을 이용한 방식은 사용자가 빠른 속도로 요청을 보내거나 했을때 겹쳐서 실행될 수 있어
// start값이 0부터 Constants.AREA_CODE.size까지 균일하게 증가하며 API 요청을 보낸다는 보장이 없었습니다
// 따라서 Coroutine을 사용하여 이 문제를 해결했습니다
class MainViewModel : ViewModel() {
    private val MAX_RETRIES = 5
    private val TAG = "MainViewModel"
    private val _locationState = MutableLiveData<LocationTrackingMode>()
    private val _items = MutableLiveData<List<HospitalItem>?>()
    private var start = 0

    var targetDepartment = ""
    var hospitalName = MutableLiveData("")

    val items: MutableLiveData<List<HospitalItem>?> = _items
    val locationState: LiveData<LocationTrackingMode> = _locationState

    var locationSource: FusedLocationSource? = null
    var job: Job? = null

    fun startGetHospitals() {
        job?.cancel()
        job = viewModelScope.launch {
            start = 0
            getHospital(start)
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun nextStep() {
        start++
        job = viewModelScope.launch {
            getHospital(start)
            delay(100L)
        }
    }

    private suspend fun getHospital(area: Int) {
        for (i in 0..MAX_RETRIES) {
            if (area < Constant.AREA_CODE.size) {
                Log.e(TAG, "current index : $area, array size: ${Constant.AREA_CODE.size}")
                try {
                    val response = ServerRepository.serverApi.hospitals(
                        serviceKey = Constant.DECODED_SERVICE_KEY,
                        sgguCd = Constant.AREA_CODE[area],
                        departmentCode = targetDepartment,
                        hospitalName = hospitalName.value?:""
                    ).await()

                    with(response) {
                        body.items.item?.let {
                            Log.e(TAG, "Data: ${it[0].addr} name: ${it[0].yadmNm}")
                            _items.postValue(it)
                        }

                        if (body.totalCount == 0) {
                            nextStep()
                        }
                    }
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "오류 : ${e.javaClass.name} $e")
                    if (e is SocketTimeoutException || e is ProtocolException) {
                        Log.e(TAG, "재시도")
                    } else {
                        break
                    }
                }
            }
        }
    }

    fun setLocationState(state: LocationTrackingMode) {
        _locationState.postValue(state)
    }
}