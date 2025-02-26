package com.example.university.model

import com.example.university.model.HospitalItem
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class HospitalList(
    @Element
    var item: List<HospitalItem>?
)