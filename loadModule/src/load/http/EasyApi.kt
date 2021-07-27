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

        headers.put("x-sign", "162554755434274F3BAB8A775464A15D6A64F82ABB534")
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
        headers.put("Cookie", "AUTH_FUND.EASTMONEY.COM_GSJZ=AUTH*TTJJ*TOKEN; qgqp_b_id=7449707830b7730c528fd8bef268bae7; em_hq_fls=js; em-quote-version=topspeed; intellpositionL=1522.39px; fund_trade_trackid=NpDDjkGjKI+osSSlk0Xu5h4kUBdfsSvtvpV0zQW2qyMLtOAYyASwB1uGFboPLBBIfRBilHNpthHgHaZW4grIHw==; cowCookie=true; intellpositionT=755px; HAList=a-sz-301017-N%u6F31%u7389%2Cf-0-000922-%u4E2D%u8BC1%u7EA2%u5229%2Cf-0-399001-%u6DF1%u8BC1%u6210%u6307%2Cf-0-000001-%u4E0A%u8BC1%u6307%u6570%2Ca-sh-605011-%u676D%u5DDE%u70ED%u7535%2Ca-sh-603171-%u7A0E%u53CB%u80A1%u4EFD%2Ca-sz-301016-C%u96F7%u5C14%u4F1F%2Ca-sz-301015-C%u767E%u6D0B%2Ca-sh-605259-%u7EFF%u7530%u673A%u68B0%2Ca-sz-301009-%u53EF%u9760%u80A1%u4EFD%2Ca-sh-603529-%u7231%u739B%u79D1%u6280%2Ca-sz-001207-%u8054%u79D1%u79D1%u6280%2Ca-sh-601528-%u745E%u4E30%u94F6%u884C; EMFUND0=07-04%2016%3A32%3A58@%23%24%u5BCC%u56FD%u9996%u521B%u6C34%u52A1REITs@%23%24508006; EMFUND1=07-04%2017%3A38%3A19@%23%24%u6613%u65B9%u8FBE%u4E2D%u8BC1%u7EA2%u5229ETF@%23%24515180; EMFUND2=07-04%2017%3A58%3A05@%23%24%u4E07%u5BB6%u4E2D%u8BC1%u7EA2%u5229%u6307%u6570%28LOF%29@%23%24161907; EMFUND3=07-04%2016%3A56%3A59@%23%24%u6613%u65B9%u8FBE%u4E2D%u8BC1%u7EA2%u5229ETF%u8054%u63A5A@%23%24009051; EMFUND4=07-04%2018%3A47%3A51@%23%24%u5357%u65B9%u4E2D%u8BC1%u94F6%u884CETF@%23%24512700; EMFUND5=07-04%2018%3A47%3A29@%23%24%u534E%u5B9D%u4E2D%u8BC1%u94F6%u884CETF@%23%24512800; EMFUND6=07-04%2019%3A15%3A00@%23%24%u5929%u5F18%u4E2D%u8BC1%u94F6%u884CETF%u8054%u63A5A@%23%24001594; EMFUND7=07-04%2019%3A15%3A06@%23%24%u5929%u5F18%u4E2D%u8BC1%u94F6%u884CETF%u8054%u63A5C@%23%24001595; EMFUND8=07-05%2012%3A20%3A48@%23%24%u534E%u590F%u4E2D%u8BC1500%u6307%u6570%u589E%u5F3AC@%23%24007995; EMFUND9=07-05 12:24:21@#$%u666F%u987A%u957F%u57CE%u5B89%u4EAB%u56DE%u62A5%u6DF7%u5408C@%23%24001423; st_pvi=07744139105282; st_inirUrl=https%3A%2F%2Fwww.baidu.com%2Flink; st_sp=2021-04-27%2023%3A18%3A54")
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
