package com.example.university.board

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.university.R
import com.example.university.SymptomHistoryActivity2
import com.example.university.databinding.ActivityBoardEditBinding
import com.example.university.utils.FBAuth
import com.example.university.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class BoardEditActivity : AppCompatActivity() {
    private lateinit var key:String
    private lateinit var binding : ActivityBoardEditBinding
    private val TAG = BoardEditActivity::class.java.simpleName
    private lateinit var writerUid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit)
        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        binding.editBtn.setOnClickListener {
            editBoardData(key)
        }
    }
    private fun editBoardData(key : String){
        FBRef.boardRef
            .child(key)
            .setValue(
                BoardModel(binding.titleArea.text.toString(),
                    binding.contentArea.text.toString(),
                    writerUid,
                    FBAuth.getTime())
            )
        Toast.makeText(this, "수정완료", Toast.LENGTH_LONG).show()

        val intent = Intent(this, SymptomHistoryActivity2::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }
    private fun getBoardData(key : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataModel = dataSnapshot.getValue(BoardModel::class.java)
//                Log.d(TAG, dataModel.toString())
//                Log.d(TAG, dataModel!!.title)
//                Log.d(TAG, dataModel!!.time)
                try {
                    binding.titleArea.setText(dataModel?.title)
                    binding.contentArea.setText(dataModel?.content)
                    writerUid = dataModel!!.uid
                } catch (e: Exception) {
                    Log.w(TAG, "loadPost:onCancelled", e)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }
}