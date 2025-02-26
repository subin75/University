package com.example.university.model

import com.example.university.model.HospitalItem
import com.naver.maps.map.overlay.Marker

data class HospitalMarker(
    val marker: Marker,
    val item: HospitalItem
)
