package com.csindex.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ICsIndexService {
    //http://47.97.204.47/syl/20210903.zip
    @GET("syl/csi{date}.zip")
    fun downloadIndustryPE(@Path("date") date: String): Call<ResponseBody>
}