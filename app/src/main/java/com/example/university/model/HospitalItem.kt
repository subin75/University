package com.example.university.model

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "item")
data class HospitalItem(
    @PropertyElement(name = "XPos") var xPos: String?,
    @PropertyElement(name = "YPos") var yPos: String?,
    @PropertyElement(name = "addr") var addr: String?,
    @PropertyElement(name = "sgguCd") var sgguCd: String?,
    @PropertyElement(name = "yadmNm") var yadmNm: String?
)