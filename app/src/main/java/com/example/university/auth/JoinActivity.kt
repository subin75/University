package com.example.university.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.bind
import androidx.databinding.DataBindingUtil.setContentView
import com.example.university.MainActivity
import com.example.university.R
import com.example.university.databinding.ActivityIntroBinding
import com.example.university.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityJoinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(this,R.layout.activity_join)
        binding.joinBtn.setOnClickListener{

            var isGoToJoin = true
            val email = binding.emailArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()

            if(email.isEmpty()) {
                Toast.makeText(this,"이메일을 입력해주세요",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(password1.isEmpty()) {
                Toast.makeText(this,"Password1 입력해주세요",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if(password2.isEmpty()) {
                Toast.makeText(this,"Password2 입력해주세요",Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(!password1.equals(password2)) {
                Toast.makeText(this, "비밀번호를 똑같이 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(password1.length < 6) {
                Toast.makeText(this,"비밀번호를 6자리 이상으로 입력해주새요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(isGoToJoin){
                auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"성공",Toast.LENGTH_LONG).show()

                            val intent = Intent(this,IntroActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,"실패",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

    }
}