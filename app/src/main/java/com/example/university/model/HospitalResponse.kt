package com.example.university.model

import com.example.university.model.Body
import com.example.university.model.Header
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class HospitalResponse(
    @Element
    var body: Body,
    @Element
    var header: Header
)
