package com.example.university

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.university.model.MedicalDepartmentItem

class MapsViewModel : ViewModel() {
    val selectedDepartment = MutableLiveData<MedicalDepartmentItem>(null)
}