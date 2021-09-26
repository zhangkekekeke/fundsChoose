package load.http

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.img.load.bean.NewShareFund
import com.img.load.bean.SpeacialValue
import load.Global
import load.bean.Share
import load.http.api.EastService
import load.util.isEmpty
import org.jsoup.select.Elements
import retrofit2.Retrofit
import java.lang.Exception

const val FCODE = "FCODE"             //基金代码
const val SHORTNAME = "SHORTNAME"     //基金简称
const val ENDNAV = "ENDNAV"           //净资产
const val SUMPLACE = "SUMPLACE"       //已获配金额（万元）
const val STKNUM = "STKNUM"           //已获配新股数
const val LISTDATE = "LISTDATE"       //上市日期

/**
 * sfname 排序类型
 */
class EasyApi {

    //近一年已获配基金
    private val url_newShares_lastYear =
        "http://fund.eastmoney.com/API/FundDXGJJ.ashx?&r=1623279106000&m=0&SFName=FCODE&IsSale=1&_=1623279106347"

    // 近一月已获配基金<br>
    private val url_newShares_lastMonth =
        "http://fund.eastmoney.com/API/FundDXGJJ.ashx?&r=1623279451000&m=8&SFName=RATIO&IsSale=1&_=1623279451246"

    // 已获配未上市新股基金<br>
    private val url_newShares_unIPO =
        "http://fund.eastmoney.com/API/FundDXGJJ.ashx?&r=1623279605000&m=1&SFName=ENDNAV&IsSale=1&_=1623279605842"

    // 特色数据<br>
    private val url_special_data = "http://fundf10.eastmoney.com/tsdata_%s.html"

    //获配新股明细：
    private val url_IPO_List =
        "http://fund.eastmoney.com/API/FundDXGJJ.ashx?m=3&sfname=LISTDATE&sorttype=desc&PageSize=0"

    var eastRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://fundsuggest.eastmoney.com")
        .build()
    val service: EastService = eastRetrofit.create(EastService::class.java)

    val httpHelper = HttpHelper()

    /**
     * 获配新股明细
     * m = 3：已获配新股明细
     * fcode ：基金代码
     * selectType=1：未上市新股
     * selectType=2：近一个月 获配新股
     * selectType=3：近一年 获配新股
     */
    fun queryIPOList(fCode: String, selectType: Int): List<Share>? {
        val url = "$url_IPO_List&fcode=$fCode&selectType=$selectType"

        val respone: String = HttpHelper().httpGet(url, null) ?: return null

        val jsonData: JsonObject = Gson().fromJson(respone, JsonObject::class.java)
        val datas = jsonData.get("Datas").asString
        val type = object : TypeToken<List<Share>>() {}.type
        try {
            return Gson().fromJson(datas, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 已获配未上市新股基金
     * size：规模
     * asc: 正序 0，desc：倒序 1
     */
    fun queryNewSharesByUnIPO(size: Int, sorttype: Int): List<NewShareFund>? {
        val url = url_newShares_unIPO + "&pagesize=$size&sorttype=${if (sorttype == 0) "asc" else "desc"}";

        val respone: String = HttpHelper().httpPost(url, null) ?: return null

        val jsonData: JsonObject = Gson().fromJson(respone, JsonObject::class.java)
        val datas = jsonData.get("Datas").asString

        val type = object : TypeToken<List<NewShareFund>>() {}.type
        return Gson().fromJson(datas, type)
    }

    /**
     * 近一年已获配基金
     * size：规模
     * asc: 正序 0，desc：倒序 1
     */
    fun queryNewSharesByYear(size: Int, sorttype: Int): List<NewShareFund>? {
        var url = url_newShares_lastYear + "&pagesize=$size&sorttype=${if (sorttype == 0) "asc" else "desc"}";

        val respone: String = HttpHelper().httpPost(url, null) ?: return null

        var jsonData: JsonObject = Gson().fromJson(respone, JsonObject::class.java)
        var datas = jsonData.get("Datas").asString

        val type = object : TypeToken<List<NewShareFund>>() {}.type
        var fundList: List<NewShareFund>? = Gson().fromJson(datas, type)
        return fundList
    }

    /**
     * 查询基金详情
     */
    fun queryFundDate(fCode: String): JsonObject? {
        val url = "https://qieman.com/pmdj/v1/funds/$fCode"
        val headers = HashMap<String, String>()

        headers.put("x-sign", Global.qie_x_sign)
        headers.put("x-request-id", Global.x_request_id)
        headers.put("sensors-anonymous-id", Global.sensors_anonymous_id)
        headers.put("Accept", "application/json")
        headers.put("Referer", "https://qieman.com/funds")
        headers.put("Sec-Fetch-Dest", "empty")
        headers.put("Sec-Fetch-Mode", "cors")
        headers.put("Sec-Fetch-Site", "same-origin")
        headers.put("Cache-Control", "no-cache")
        headers.put(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36"
        )
        headers.put(
            "Authorization",
            "Bearer eyJ2ZXIiOiJ2MSIsImFsZyI6IkhTNTEyIn0.eyJzdWIiOiIxMjI1NTg3IiwiaXNzIjoic3NvLnFpZW1hbi5jb20iLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjI0NTMzMzkwLCJpYXQiOjE2MjMyMzczOTAsImlzQXBwbGVVc2VyTm9QaG9uZSI6ZmFsc2UsImp0aSI6IjJiODEzYzk5LTk5MWYtNDliZS05OWU2LTVkZjdlYzdhNWQ0MiJ9.-h_gNZlDtnq07xhGDeFuuFAN95_ayk9fF-uk9bMuGxcsKKg2G0mbzUoC88STXHeFDeUCXYrkSy0YQwJQqDblEw"
        )
        val httpHelper = HttpHelper()
        val respone: String? = httpHelper.httpGet(url, headers)
        var jsonData: JsonObject? = null
        try {
            jsonData = Gson().fromJson(respone, JsonObject::class.java)
        } catch (ignore: Exception) {
            ignore.printStackTrace()
            httpHelper.httpCache.delete(url)
        }
        return jsonData
    }

    /**
     * 用基金名称，查询分级基金集合
     */
    fun queryACFund(name: String): JsonObject? {
        //特殊条件：且慢查询系统，需要剔除(LOF)后缀，不然会查询错误
        val newName = name.replace("(LOF)", "")
        val url = "http://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx?m=1&_=1623420782081"
//        val url = "https://qieman.com/pmdj/v1/search/funds?q=${URLEncoder.encode(newName, "utf-8")}&limit=5&tradableFunds=1&includeInvestPoolTag=0"
        val headers = HashMap<String, String>()
        headers.put(
            "Cookie",
            Global.eastCookie
        )
        headers.put(
            "Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        )
        headers.put("Accept-Encoding", "gzip, deflate")
        headers.put("Cache-Control", "no-cache")
        headers.put("Accept-Language", "zh-CN,zh;q=0.9")
        headers.put(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36"
        )

        var jsonData: JsonObject? = null
        val respones = httpHelper.httpCache.check(url)
        if (!isEmpty(respones)) {
            try {
                return Gson().fromJson(respones, JsonObject::class.java)
            } catch (ignore: Exception) {
                httpHelper.httpCache.delete(url)
            }
        }

        val respone2 = service.easyFundSeachApi(newName, headers).execute()
        var body: String?
        if (respone2.isSuccessful) {
            try {
                println("正在请求网络：${respone2.raw().request.url}")
                respone2.body()?.bytes()?.let {
                    body = String(it)
                    httpHelper.httpCache.save(url, body)
                    jsonData = Gson().fromJson(body, JsonObject::class.java)
                }
            } catch (ignore: Exception) {
                ignore.printStackTrace()
                httpHelper.httpCache.delete(url)
            }
        } else {
            httpHelper.httpCache.delete(url)
        }

        return jsonData
    }

    /**
     * 天天基金，特色数据
     */
    fun querySpecialData(fund: NewShareFund, fcode: String) {
        val url = String.format(url_special_data, fcode)

        val document = JsoupClient().getJsoup(url);

        val element: Elements = document.select("table[class=fxtb]").get(0).select("tr")
        for (trElement in element) {
            val tdElement = trElement.select("td")
            if (tdElement.size == 0)
                continue

            if ("标准差".equals(tdElement.get(0).text())) {
                val sdValue = SpeacialValue("标准差")
                sdValue.oneYear = tdElement.get(1).text().replace("%", "").replace("--", "-1").toFloat()
                sdValue.twoYear = tdElement.get(2).text().replace("%", "").replace("--", "-1").toFloat()
                sdValue.threeYear = tdElement.get(3).text().replace("%", "").replace("--", "-1").toFloat()
                fund.sd = sdValue
            } else if ("夏普比率".equals(tdElement.get(0).text())) {
                val srValue = SpeacialValue("夏普比率")
                srValue.oneYear = tdElement.get(1).text().replace("--", "-1").toFloat()
                srValue.twoYear = tdElement.get(2).text().replace("--", "-1").toFloat()
                srValue.threeYear = tdElement.get(3).text().replace("--", "-1").toFloat()
                fund.sr = srValue
            } else if ("信息比率".equals(tdElement.get(0).text())) {
                val infoR = SpeacialValue("信息比率")
                infoR.oneYear = tdElement.get(1).text().replace("--", "").let {
                    if (it.isEmpty()) {
                        return@let null
                    }
                    return@let it.toFloat()
                }
                infoR.twoYear = tdElement.get(2).text().replace("--", "").let {
                    if (it.isEmpty()) {
                        return@let null
                    }
                    return@let it.toFloat()
                }
                infoR.threeYear = tdElement.get(3).text().replace("--", "").let {
                    if (it.isEmpty()) {
                        return@let null
                    }
                    return@let it.toFloat()
                }
                fund.infoR = infoR
            }
        }
        val elementInfoR: Elements = document.select("table[class=fxtb]").let {
            if (it.size >= 2)
                return@let it[1].select("tr")
            else return@let null
        } ?: return
        for (trElement in elementInfoR) {
            val tdElement = trElement.select("td")
            if (tdElement.size == 0)
                continue

            if (tdElement[1].text().isNotEmpty()) {//跟踪误差
                val value = tdElement[1].text().replace("%", "").replace("--", "-1").toFloat()
                fund.travErrorR = value
            }
            if (tdElement[2].text().isNotEmpty()) {//同类平均跟踪误差
                val value = tdElement[2].text().replace("%", "").replace("--", "-1").toFloat()
                fund.averErrorR = value
            }
        }
    }

    /**
     * 天天基金，规模
     */
    fun queryEndNav(fcode: String): Float {
        val url_EndNav_fund = "http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jzcgm&code=$fcode"

        var respone: String = HttpHelper().httpGet(url_EndNav_fund, null) ?: return 0f
        respone = respone.replace("var jzcgm_apidata=", "")

        val array: JsonArray = Gson().fromJson(respone, JsonArray::class.java)
        val million100 = 100000000f
        if (array.size() == 0) return 0f
        return (array.get(array.size() - 1) as JsonArray).get(1).asFloat * million100;
    }
}
