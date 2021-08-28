package load.http

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.img.load.bean.NewShareFund
import com.img.load.bean.SpeacialValue
import load.bean.Share
import org.jsoup.select.Elements
import java.lang.Exception
import java.net.URLEncoder

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
    private val url_newShares_lastYear = "http://fund.eastmoney.com/API/FundDXGJJ.ashx?&r=1623279106000&m=0&SFName=FCODE&IsSale=1&_=1623279106347"

    // 近一月已获配基金<br>
    private val url_newShares_lastMonth = "http://fund.eastmoney.com/API/FundDXGJJ.ashx?&r=1623279451000&m=8&SFName=RATIO&IsSale=1&_=1623279451246"

    // 已获配未上市新股基金<br>
    private val url_newShares_unIPO = "http://fund.eastmoney.com/API/FundDXGJJ.ashx?&r=1623279605000&m=1&SFName=ENDNAV&IsSale=1&_=1623279605842"

    // 特色数据<br>
    private val url_special_data = "http://fundf10.eastmoney.com/tsdata_%s.html"

    //获配新股明细：
    private val url_IPO_List = "http://fund.eastmoney.com/API/FundDXGJJ.ashx?m=3&sfname=LISTDATE&sorttype=desc&PageSize=0"

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
            return  Gson().fromJson(datas, type)
        }catch (e:Exception){
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

        headers.put("x-sign", "16295178769179402AD82E6010E7C317122EDA5731874")
        headers.put("Accept", "application/json")
        headers.put("x-aid", "A.97DB7BD01F7R7SG19QGD7XEBYGA27J1TA")
        headers.put("x-request-id", "albus.36EE6CA93A63C1A71CEA")
        headers.put("sensors-anonymous-id", "179f07ccf2e129-030f0c7c2dd2ca-3e604809-2073600-179f07ccf2f67e")
        headers.put("Referer", "https://qieman.com/funds")
        headers.put("Sec-Fetch-Dest", "empty")
        headers.put("Sec-Fetch-Mode", "cors")
        headers.put("Sec-Fetch-Site", "same-origin")
        headers.put("Cache-Control", "no-cache")
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
        headers.put("Authorization", "Bearer eyJ2ZXIiOiJ2MSIsImFsZyI6IkhTNTEyIn0.eyJzdWIiOiIxMjI1NTg3IiwiaXNzIjoic3NvLnFpZW1hbi5jb20iLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjI0NTMzMzkwLCJpYXQiOjE2MjMyMzczOTAsImlzQXBwbGVVc2VyTm9QaG9uZSI6ZmFsc2UsImp0aSI6IjJiODEzYzk5LTk5MWYtNDliZS05OWU2LTVkZjdlYzdhNWQ0MiJ9.-h_gNZlDtnq07xhGDeFuuFAN95_ayk9fF-uk9bMuGxcsKKg2G0mbzUoC88STXHeFDeUCXYrkSy0YQwJQqDblEw")
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
        val url = "http://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx?m=1&key=${URLEncoder.encode(newName, "utf-8")}&_=1623420782081"
//        val url = "https://qieman.com/pmdj/v1/search/funds?q=${URLEncoder.encode(newName, "utf-8")}&limit=5&tradableFunds=1&includeInvestPoolTag=0"
        val headers = HashMap<String, String>()
        headers.put("Cookie", "AUTH_FUND.EASTMONEY.COM_GSJZ=AUTH*TTJJ*TOKEN; qgqp_b_id=7449707830b7730c528fd8bef268bae7; em_hq_fls=js; em-quote-version=topspeed; intellpositionL=1522.39px; intellpositionT=605px; HAList=a-sz-000933-%u795E%u706B%u80A1%u4EFD%2Ca-sz-002585-%u53CC%u661F%u65B0%u6750%2Ca-sh-601636-%u65D7%u6EE8%u96C6%u56E2%2Ca-sz-300037-%u65B0%u5B99%u90A6%2Ca-sz-300671-%u5BCC%u6EE1%u7535%u5B50%2Ca-sh-600884-%u6749%u6749%u80A1%u4EFD%2Ca-sh-600036-%u62DB%u5546%u94F6%u884C%2Ca-sh-601012-%u9686%u57FA%u80A1%u4EFD%2Ca-sh-600276-%u6052%u745E%u533B%u836F%2Ca-sz-301047-%u4E49%u7FD8%u795E%u5DDE%2Cd-hk-03033%2Ca-sh-605011-%u676D%u5DDE%u70ED%u7535; st_si=98812511709207; st_asi=delete; fund_trade_trackid=bi/JWP6p7p8ixzKGTkiFjk8iw59M3E+beOpR6tg+yIsJJspct5VWRA8amR2qYnRgjEJQNfdK9JZDW93ZJeiNZw==; LToken=9f20438b470742098c93e56439066a42; FundWebTradeUserInfo=JTdCJTIyQ3VzdG9tZXJObyUyMjolMjJIMSUyNTJmTWFJY0VLRGh4V2s3aG1WalhXZlYxJTI1MmZNdTFUUUJFYTRwVW9nNm0yYmVqMURTejFrJTI1MmJ5U0NlbWc2UDZqdE9qRmpnd20lMjUyZmJibkUyazBtcEJjbFpxRlNXdVI2YVhCREwwTiUyNTJmemtRM2tBVm83V3hpWlhuOGclMjUzZCUyMiwlMjJDdXN0b21lck5hbWUlMjI6JTIyMTU5KioqKjM5NzYlMjIsJTIyVmlwTGV2ZWwlMjI6JTIyMCUyMiwlMjJMVG9rZW4lMjI6JTIyOWYyMDQzOGI0NzA3NDIwOThjOTNlNTY0MzkwNjZhNDIlMjIsJTIySXNWaXNpdG9yJTIyOiUyMnRydWUlMjIsJTIyUmlzayUyMjolMjIzJTIyJTdE; EMFUND1=08-21%2009%3A20%3A59@%23%24%u56FD%u6295%u4E2D%u8BC1500%u6307%u6570%u91CF%u5316%u589E%u5F3AA@%23%24005994; EMFUND0=08-21%2009%3A18%3A29@%23%24%u535A%u9053%u4E2D%u8BC1500%u589E%u5F3AC@%23%24006594; EMFUND2=08-21%2009%3A21%3A08@%23%24%u56FD%u6295%u4E2D%u8BC1500%u6307%u6570%u91CF%u5316%u589E%u5F3AC@%23%24007089; EMFUND3=08-21%2011%3A40%3A05@%23%24%u666F%u987A%u957F%u57CE%u4E2D%u8BC1500%u589E%u5F3A@%23%24006682; EMFUND4=08-21%2011%3A44%3A33@%23%24%u897F%u90E8%u5229%u5F97%u91CF%u5316%u6210%u957F%u6DF7%u5408A@%23%24000006; EMFUND5=08-21%2010%3A15%3A29@%23%24%u5E7F%u53D1%u591A%u56E0%u5B50%u6DF7%u5408@%23%24002943; EMFUND7=08-21%2011%3A42%3A41@%23%24%u5BCC%u56FD%u5929%u60E0%u6210%u957F%u6DF7%u5408A/B%28LOF%29@%23%24161005; EMFUND8=08-21%2011%3A26%3A06@%23%24%u5BCC%u56FD%u5929%u535A%u521B%u65B0%u4E3B%u9898%u6DF7%u5408@%23%24519035; EMFUND9=08-21%2011%3A46%3A53@%23%24%u5BCC%u56FD%u5929%u60E0%u6210%u957F%u6DF7%u5408C@%23%24003494; EMFUND6=08-21 11:48:15@#$%u666F%u987A%u957F%u57CE%u65B0%u5174%u6210%u957F%u6DF7%u5408@%23%24260108; st_pvi=07744139105282; st_sp=2021-04-27%2023%3A18%3A54; st_inirUrl=https%3A%2F%2Fwww.baidu.com%2Flink; st_sn=119; st_psi=20210821115305251-112200312939-4415002056")
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        headers.put("Accept-Encoding", "gzip, deflate")
        headers.put("Cache-Control", "no-cache")
        headers.put("Accept-Language", "zh-CN,zh;q=0.9")
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
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
