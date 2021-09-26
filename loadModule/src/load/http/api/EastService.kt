package load.http.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query


interface EastService {
    @GET("FundSearch/api/FundSearchAPI.ashx?m=1&_=1623420782081")
    fun easyFundSeachApi(
        @Query("key") key: String,
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

}