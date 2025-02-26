package com.example.university

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.util.FusedLocationSource

class MainActivity : AppCompatActivity() {
    var TAG: String = "로그"
    private lateinit var auth: FirebaseAuth

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel.locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "MapsActivity - onRequestPermissionsResult")
        val locationSource = mainViewModel.locationSource
        if (locationSource != null) {
            if (locationSource!!.onRequestPermissionsResult(
                    requestCode, permissions,
                    grantResults
                )
            ) {
                if (!locationSource!!.isActivated) { // 권한 거부됨
                    Log.d(TAG, "MainActivity - onRequestPermissionsResult 권한 거부됨")
                    mainViewModel.setLocationState(LocationTrackingMode.None)
                } else {
                    Log.d(TAG, "MainActivity - onRequestPermissionsResult 권한 승인됨")
                    mainViewModel.setLocationState(LocationTrackingMode.Follow) // 현위치 버튼 컨트롤 활성
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}

//      findViewById<Button>(R.id.logoutBtn).setOnClickListener {
//         auth.signOut()

//         val intent = Intent(this, IntroActivity::class.java)
//         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//     }
// }
//}