package com.example.university.model

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "header")
data class Header(
    @PropertyElement(name = "resultCode")
    var resultCode: Int,
    @PropertyElement(name = "resultMsg")
    var resultMsg: String,
)