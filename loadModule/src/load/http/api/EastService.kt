package load.http.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.net.URLEncoder


interface EastService {
    @GET("FundSearch/api/FundSearchAPI.ashx?m=1&_=1623420782081")
    fun easyFundSeachApi(
        @Query("key") key: String,
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

}