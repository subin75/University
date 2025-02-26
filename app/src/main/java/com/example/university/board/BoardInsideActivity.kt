package com.example.university.board

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.university.R
import com.example.university.databinding.ActivityBoardInsideBinding
import com.example.university.utils.FBAuth
import com.example.university.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class BoardInsideActivity : AppCompatActivity() {
    private val TAG = BoardInsideActivity::class.java.simpleName
    private lateinit var binding : ActivityBoardInsideBinding
    private lateinit var key:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside)
        binding.boardSettingIcon.setOnClickListener {
            showDialog()
        }
        // 두번째 방법
        key = intent.getStringExtra("key").toString()
        getBoardData(key)
    }
    private fun showDialog(){
        try {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("게시글 수정/삭제")
            val alertDialog = mBuilder.show()
            alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {
                Toast.makeText(this, "수정 버튼을 눌렀습니다", Toast.LENGTH_LONG).show()
                val intent = Intent(this, BoardEditActivity::class.java)
                intent.putExtra("key",key)
                startActivity(intent)
            }
            alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener {
                FBRef.boardRef.child(key).removeValue()
                Toast.makeText(this, "삭제완료", Toast.LENGTH_LONG).show()
                finish()
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }
    private fun getBoardData(key : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(TAG, dataModel!!.title)
                    binding.titleArea.text = dataModel!!.title
                    binding.textArea.text = dataModel!!.content
                    binding.timeArea.text = dataModel!!.time
                    val myUid = FBAuth.getUid()
                    val writerUid = dataModel.uid
                    if(myUid.equals(writerUid)){
                        Log.d(TAG, "내가 쓴 글")
                        binding.boardSettingIcon.isVisible = true
                    } else {
                        Log.d(TAG, "내가 쓴 글 아님")
                    }
                } catch (e : Exception){
                    Log.d(TAG, "삭제완료")
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