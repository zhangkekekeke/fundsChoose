package com.csindex.model

import com.csindex.api.ICsIndexService
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Retrofit
import java.io.File

class IndustryModule {
    val INDUSTRY_PE_ROOT="G:\\行业估值\\"

    //http://47.97.204.47/syl/csi20210903.zip
    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://47.97.204.47")
        .build()
    val service: ICsIndexService = retrofit.create(ICsIndexService::class.java)

    fun downloadIndustryPE(date: String, callback: Callback<ResponseBody>) {
        return service.downloadIndustryPE(date)
            .enqueue(callback)
    }

    /* 保存文件 */
    fun saveDate(name: String, img: ByteArray) {
        val file=File(INDUSTRY_PE_ROOT, "$name.zip")
        if(!file.parentFile.exists()){
            file.parentFile.mkdirs()
        }

        file.writeBytes(img)
    }
}