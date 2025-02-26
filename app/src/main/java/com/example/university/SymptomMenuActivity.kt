package com.example.university

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SymptomMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom_menu)

        val btnSymptomHistory: ImageView = findViewById(R.id.btn_symptom_history)
        btnSymptomHistory.setOnClickListener {
            // 증상기록 화면으로 이동
            startActivity(Intent(this@SymptomMenuActivity, SymptomHistoryActivity2::class.java))
        }
    }

}