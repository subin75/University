package com.example.university.board

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.university.R
import com.example.university.databinding.ActivityBoardWriteBinding
import com.example.university.utils.FBAuth
import com.example.university.utils.FBRef

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardWriteBinding
    private val TAG = "BoardWriteActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_board_write)

        binding.writeBtn.setOnClickListener {
            val title = binding.titleArea.text.toString()
            val context = binding.contentArea.text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            Log.d(TAG, title)
            Log.d(TAG, context)

            val key = FBRef.boardRef.push().key.toString()
            FBRef.boardRef
                .child(key)
                .setValue(BoardModel(title,context,uid,time))

            Toast.makeText(this,"입력 완료", Toast.LENGTH_LONG).show()

            finish()
        }
    }
}