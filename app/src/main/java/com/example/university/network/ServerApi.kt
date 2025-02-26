package com.example.university.network

import com.example.university.model.HospitalResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    // + 병원명으로 검색하는 쿼리 추가
    @GET("getHospBasisList")
    fun hospitals(@Query(value = "serviceKey", encoded = true) serviceKey: String,
                  @Query(value = "numOfRows") rows: Int = 40,
                  @Query(value = "sgguCd") sgguCd: String,
                  @Query(value = "dgsbjtCd") departmentCode: String = "",
                  @Query(value = "yadmNm") hospitalName: String = ""
    ): Call<HospitalResponse>
}