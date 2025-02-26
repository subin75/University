package com.example.university

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.university.board.BoardInsideActivity
import com.example.university.board.BoardListLVAdapter
import com.example.university.board.BoardModel
import com.example.university.board.BoardWriteActivity
import com.example.university.databinding.ActivitySymptomHistory2Binding
import com.example.university.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * 증상기록 화면
 */
class SymptomHistoryActivity2 : AppCompatActivity() {

    private lateinit var binding : ActivitySymptomHistory2Binding
    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()
    private val TAG = SymptomHistoryActivity2::class.java.simpleName
    private lateinit var boardRVAdapter : BoardListLVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom_history2)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_symptom_history2)
        boardRVAdapter = BoardListLVAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter
        binding.boardListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, BoardInsideActivity::class.java)
            intent.putExtra("key", boardKeyList[position])
            startActivity(intent)
        }
        binding.writeBtn.setOnClickListener {
            val intent = Intent(this, BoardWriteActivity::class.java)
            startActivity(intent)
        }

        getFBBoardData()
    }

    private fun getFBBoardData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardDataList.clear()
                for (dataModel in dataSnapshot.children) {
                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    boardDataList.add(item!!)
                    boardKeyList.add(dataModel.key.toString())
                }
                boardKeyList.reverse()
                boardDataList.reverse()
                boardRVAdapter.notifyDataSetChanged()
                Log.d(TAG, boardDataList.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.addValueEventListener(postListener)
    }
}