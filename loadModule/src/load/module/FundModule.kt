package load.module

import com.google.gson.JsonElement
import com.img.load.bean.NewShareFund
import load.ChooseData
import load.ChooseType
import load.FundSort
import load.bean.Share
import load.http.EasyApi
import load.util.isEmpty
import java.text.SimpleDateFormat
import java.util.*

interface FundCallback {
    fun onFundsCount(count: Int);
    fun onFundsFilter(filterFunds: List<NewShareFund>);
}

class FundModule {
    var easyApi = EasyApi()

    /* 筛选打新基金*/
    fun filterNewShareFunds(chooseData: ChooseData, callback: FundCallback) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

        val funds: List<NewShareFund> = easyApi.queryNewSharesByYear(0, 1) ?: return

        System.out.println("打新基金总数：" + funds.size)
        callback.onFundsCount(funds.size)//回调

        val million100 = 100000000f
        val newFunds = arrayListOf<NewShareFund>()
        //开始筛选 ，将符合条件的加入数组

        //第一轮筛选，找出符合条件的A类基金
        funds.filter {
            val filterName = chooseData.fuzzyName?.let { list ->
                var result = list.isEmpty()
                for (name in list) {//按名字筛选
                    if (!result)
                        result = it.SHORTNAME.contains(name)
                    else continue
                }
                return@let result
            }

            filterName ?: true && !ignorSpecialFund(it)//手动过滤，不符合要求的基金
                    && !it.ENDNAV.isNullOrEmpty()
        }.forEach {
            val fund = it
            val endNav = fund.ENDNAV!!.toFloat()

//          //条件0：剔除C类基金, 稍后合并
            if (fund.SHORTNAME.contains("C") || (fund.SHORTNAME.contains("B")&&!fund.SHORTNAME.contains("A/B"))
                    || fund.SHORTNAME.contains("D") || fund.SHORTNAME.contains("I")
                    || fund.SHORTNAME.contains("定开") || fund.SHORTNAME.contains("定期"))
                return@forEach
            if (!fund.SHORTNAME.contains("ETF") && fund.SHORTNAME.contains("E") || fund.SHORTNAME.contains("ETF联接E"))
                return@forEach

            //条件1：A类 规模
            if (endNav < chooseData.minEndNav * million100 || endNav > chooseData.maxEndNav * million100)
                return@forEach

            //Http请求：基金详情
            val jsonObject = easyApi.queryFundDate(fund.FCODE) ?: return@forEach

            //条件2：股票占比 < x
            val jsonArray = jsonObject.getAsJsonArray("assetPortfolio") ?: return@forEach
            var ratio: Float? = null
            for (item in jsonArray) {
                if ("股票".equals(item.asJsonObject.get("name").asString)) {
                    ratio = item.asJsonObject.get("ratio").asString.replace("%", "").toFloat()
                    continue
                }
            }
            if (ratio == null || chooseData.maxShareRatio ?: 100f < ratio
                    || chooseData.minShareRatio ?: 0f > ratio)
                return@forEach
            fund.shareRatio = ratio.div(100)

            //条件3：成立时间 > x年
            fund.setupDate = dataFormat.parse(jsonObject.get("setupDate").asString) ?: return@forEach
            val setupDate = Calendar.getInstance()
            setupDate.time = fund.setupDate
            val year = Calendar.getInstance().get(Calendar.YEAR) - setupDate.get(Calendar.YEAR)
            if (year < chooseData.year) return@forEach


            //Http请求：特色数据
            easyApi.querySpecialData(fund, fund.FCODE)

            //条件4：三年夏普比 > x，二年夏普比 > x，一年夏普比 > x
            if (fund.sr?.oneYear!! < chooseData.srOneYear ?: -Float.MAX_VALUE
                    || fund.sr?.twoYear!! < chooseData.srTwoYear ?: -Float.MAX_VALUE
                    || fund.sr?.threeYear!! < chooseData.srThreeYear ?: -Float.MAX_VALUE)
                return@forEach

            //条件5：三年标准差 < x，二年标准差 < x，一年标准差 < x
            if (fund.sd?.oneYear!! > chooseData.sdOneYear ?: Float.MAX_VALUE
                    || fund.sd?.twoYear!! > chooseData.sdTwoYear ?: Float.MAX_VALUE
                    || fund.sd?.threeYear!! > chooseData.sdThreeYear ?: Float.MAX_VALUE)
                return@forEach

            if (chooseData.chooseType == ChooseType.FUND_500) {
                //条件：跟踪误差
//                if (fund.travErrorR ?: 0f > fund.averErrorR ?: 1f)
//                    return@forEach
                //条件：信息比率
                if (fund.infoR!!.oneYear ?: Float.MAX_VALUE <= chooseData.infoROneYear ?: -Float.MAX_VALUE
                        || fund.infoR!!.twoYear ?: Float.MAX_VALUE <= chooseData.infoRTwoYear ?: -Float.MAX_VALUE
                        || fund.infoR!!.threeYear ?: Float.MAX_VALUE <= chooseData.infoRThreeYear ?: -Float.MAX_VALUE
                )
                    return@forEach
            }
            newFunds.add(fund)
        }

        //获取 近一年 获配新股
        for (filterFund in newFunds) {
            filterFund.earningTime = chooseData.earningTime
            val unIPOList: List<Share> = easyApi.queryIPOList(filterFund.FCODE, 3) ?: return
            for (unIPOShare in unIPOList) {
                if (ignoreShare(unIPOShare) || unIPOShare.LISTPRICE.isEmpty() || unIPOShare.ISSUEPRICE == null) continue
                //上市时间 制定时间内
                if (chooseData.earningTime * 30 <= (Date().time - dataFormat.parse(unIPOShare.LISTDATE).time) / 1000 / 60 / 60 / 24)
                    continue

                filterFund.earningRateByTime += (unIPOShare.LISTPRICE.toFloat() / unIPOShare.ISSUEPRICE!!.toFloat() - 1) *
                        unIPOShare.PCTNAV.let {
                            if (it.isEmpty()) return@let 0f
                            return@let it.toFloat()
                        }
            }
        }

        //第二轮筛选，A类与C类基金，合并规模。再次筛选
        for (fundA in newFunds.reversed()) {
            val isA=fundA.SHORTNAME.contains("A")
            val shortName = if(isA) fundA.SHORTNAME.substring(0, fundA.SHORTNAME.indexOf("A")) else fundA.SHORTNAME
            //找出对应的其他类基金，如:C、B、D
            val fundOther: ArrayList<NewShareFund> = arrayListOf()
//            for (originalFund in funds.reversed()) {
//                if (!originalFund.SHORTNAME.equals(fundA.SHORTNAME)
//                        && originalFund.SHORTNAME.contains(shortName)) {
//                    fundOther.add(originalFund)
//                }
//            }

            //合并规模
            var totalEndNav = 0f
            if (fundOther.size == 0) {
                //再次查询
                val responeObject = easyApi.queryACFund(shortName)
                val dataArray = responeObject?.get("Datas")?.asJsonArray
                if (dataArray == null || dataArray.size() == 0) {
                    if (!ignorOtherFund(fundA)) {
//                        RuntimeException("找不到对应的 其他类基金：" + fundA.SHORTNAME).printStackTrace()
                    }
                    fundA.TotalEndNav = totalEndNav//没有分类，记录自己为总规模
                } else {
                    for (json: JsonElement in dataArray) {
                        val jsonObject = json.asJsonObject
                        if (jsonObject.get("NAME").asString.contains("后端"))
                            continue//踢出 后端

                        val endnav = easyApi.queryEndNav(jsonObject.get("CODE").asString)
                        totalEndNav += endnav
                    }
                }

            } else {
                for (other in fundOther) {
                    if (isEmpty(other.ENDNAV))
                        continue
                    totalEndNav += other.ENDNAV!!.toFloat()
                }
            }
            //记录数据
            fundA.TotalEndNav = totalEndNav

            val unIPOList: List<Share> = easyApi.queryIPOList(fundA.FCODE, 1) ?: return
            for (unIPOShare in unIPOList) {
                if (ignoreShare(unIPOShare)|| unIPOShare.SUMPLACE*100/fundA.TotalEndNav!!>0.13f) continue
                fundA.sharesUnIPO.SUMPLACE += unIPOShare.SUMPLACE
                fundA.sharesUnIPO.STKNUM += unIPOShare.SHAREPLACE
            }

            //条件1：A、C类 规模 <x亿
            if (totalEndNav > chooseData.maxEndNav * million100) {
                newFunds.remove(fundA)
                continue
            }

            //条件：未上市新股，占比 < 0.25%
            val unIPORatio: Float = fundA.sharesUnIPO.SUMPLACE * 100 / totalEndNav / fundA.shareRatio!!
            if (unIPORatio < 0.12f) {
//                newFunds.remove(fundA)
//                continue
            }

            //条件3: A、C类， 未上市 / 股票规模 > 0.4%
            val unIpoRatio = fundA.sharesUnIPO.SUMPLACE / totalEndNav
//            if (unIpoRatio / fundA.shareRatio!!.toFloat() < 0.001) {//获配数据是共享的，不用相加
//                newFunds.remove(fundA)
//                continue
//            }
            //条件3: A、C类， 未上市 / 基金总规模 > 0.4%
//            val unIpoRatio2 = fundA.sharesUnIPO.SUMPLACE / totalEndNav
//            if (unIpoRatio2 < 0.0001) {//获配数据是共享的，不用相加
//                newFunds.remove(fundA)
//                continue
//            }

            //组合条件1：高未上市比 + 高三年夏普 || 一年夏普垃圾
//            if (unIpoRatio < 0.005 && fundA.sr?.threeYear!! < 2.1 || fundA.sr?.oneYear!! < 2.3) {
//                newFunds.remove(fundA)
//                continue
//            }

//            println(fundA.SHORTNAME + "  新股数:" + fundA.STKNUM + " 新股占比:" + (fundA.SUMPLACE * 100 / totalEndNav).toInt()
//                    + "% 规模:" + String.format("%.1f", totalEndNav / million100) + " 股票占比:" + fundA.shareRatio + "%"
//                    + " 三年标准差:" + fundA.sd!!.oneYear + "%" + " 三年夏普比:" + fundA.sr!!.threeYear)
        }

        //排序：根据未上市新股 占比
        sortByUnIPO(chooseData, newFunds)

        System.out.println("符合条件的：" + newFunds.size)
        callback.onFundsFilter(newFunds)
    }

    private fun ignorSpecialFund(fundA: NewShareFund): Boolean {
        return fundA.SHORTNAME.contains("招商和悦稳健养老")
                || fundA.SHORTNAME.contains("招商和悦稳健养老")
                || fundA.SHORTNAME.contains("中证500质量")
    }

    private fun ignorOtherFund(fundA: NewShareFund): Boolean {
        return fundA.SHORTNAME.contains("上投摩根双息平衡混合")
                || fundA.SHORTNAME.contains("嘉实研究精选混合")
                || fundA.SHORTNAME.contains("大成内需增长混合")
                || fundA.SHORTNAME.contains("鹏华中证A股资源产业指数")
                || fundA.SHORTNAME.contains("中银收益混合A")
                || fundA.SHORTNAME.contains("银华-道琼斯88指数A")
                || fundA.SHORTNAME.contains("招商行业领先混合A")
                || fundA.SHORTNAME.contains("景顺长城核心竞争力混合A")
                || fundA.SHORTNAME.contains("广发行业领先混合A")
                || fundA.SHORTNAME.contains("上投摩根行业轮动混合A")
                || fundA.SHORTNAME.contains("国富潜力组合混合A")
    }

    //忽略的新股。如：蚂蚁集团
    private fun ignoreShare(share: Share): Boolean {
        return share.STKNAME.contains("蚂蚁集团")
                || share.STKNAME.contains("苏州恒久")
    }

    fun sortByUnIPO(chooseData: ChooseData, newFunds: ArrayList<NewShareFund>) {

        Collections.sort(newFunds, object : Comparator<NewShareFund> {
            override fun compare(o1: NewShareFund, o2: NewShareFund): Int {
                var value2 = 0f
                var value1 = 0f
                when (chooseData.fundSort) {
                    FundSort.ONE_YEAR_SpeacialValue -> {
                        //近一段时间 已获配金额占当期规模比例
                        value2 = o2.sr?.oneYear?:0f
                        value1 = o1.sr?.oneYear?:0f
                    }
                    FundSort.TWO_YEAR_SpeacialValue -> {
                        //近一段时间 已获配金额占当期规模比例
                        value2 = o2.sr?.twoYear?:0f
                        value1 = o1.sr?.twoYear?:0f
                    }
                    FundSort.Three_YEAR_SpeacialValue -> {
                        //近一段时间 已获配金额占当期规模比例
                        value2 = o2.sr?.threeYear?:0f
                        value1 = o1.sr?.threeYear?:0f
                    }
                    FundSort.earningRateByTime -> {
                        //近一段时间 已获配金额占当期规模比例
                        value2 = o2.earningRateByTime
                        value1 = o1.earningRateByTime
                    }
                    FundSort.UNIPO_EndNAV_RATE -> {
                        //未上市新股市值 / 基金规模
                        value2 = o2.sharesUnIPO.SUMPLACE / (o2.TotalEndNav!!.toFloat())
                        value1 = o1.sharesUnIPO.SUMPLACE / (o1.TotalEndNav!!.toFloat())
                    }
                    FundSort.UNIPO_ShareNav_RATE -> {
                        //未上市新股市值 / 股票规模
                        value2 = o2.sharesUnIPO.SUMPLACE / (o2.TotalEndNav!!.toFloat() * o2.shareRatio!!.toFloat())
                        value1 = o1.sharesUnIPO.SUMPLACE / (o1.TotalEndNav!!.toFloat() * o1.shareRatio!!.toFloat())
                    }
                }

                //近一年获配新股 / 基金规模
//                 value2 = o2.SUMPLACE * 100 / (o2.TotalEndNav!!.toFloat() * o2.shareRatio!!.toFloat())
//                 value1 = o1.SUMPLACE * 100 / (o1.TotalEndNav!!.toFloat() * o1.shareRatio!!.toFloat())
                return if (value2 > value1) 1 else -1
            }
        }
        )
    }
}