package com.example.university.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "body")
data class Body(
    @Element
    var items: HospitalList,

    @PropertyElement(name = "numOfRows")
    var numOfRows: Int,
    @PropertyElement(name = "pageNo")
    var pageNo: Int,
    @PropertyElement(name = "totalCount")
    var totalCount: Int
)