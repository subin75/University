package com.example.university.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.university.R
import com.example.university.SymptomMenuActivity
import com.example.university.auth.IntroActivity
import com.example.university.auth.LoginActivity
import com.example.university.databinding.FragmentBookmarkBinding
import com.google.firebase.auth.FirebaseAuth

class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bookmark,container,false)


        binding.btnSymptomMenu.setOnClickListener {
            // 증상기록 선택 메뉴 화면으로 이동
            requireContext().startActivity(Intent(requireContext(), SymptomMenuActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            // 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 로그인 화면으로 복귀하며, 기존에 있던 액티비티나 프래그먼트 스택들을 모두 날려버린채로 클린하게 이동함.
            val intent = Intent(requireContext(), IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            requireActivity().finish()
        }


        binding.talkTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)
        }

        binding.homeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }
        return binding.root
    }

}