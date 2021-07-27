package com.img.load.bean

import java.util.*
import kotlin.collections.ArrayList

//基金
open class Fund {
    var FCODE: String = ""
    var SHORTNAME: String = ""

    var ENDNAV: String? = null//净资产规模
    var TotalEndNav: Float? = null//A+C类，合并净资产规模

    var shareRatio: Float? = null//股票比例
    var setupDate: Date? = null//成立时间

    //特殊数据
    var sd: SpeacialValue? = null//标准差
    var sr: SpeacialValue? = null//夏普比
    var infoR: SpeacialValue? = null//信息比率
    var travErrorR: Float? = null//跟踪误差
    var averErrorR: Float? = null//同类平均跟踪误差
    var Maxdrop: Float? = null//最大回撤
    var netWorths: List<NetWorth>? = null//历史净值

}

class NewShareFund : Fund() {
    //新股相关
    var SUMPLACE: Float = 0f       //已获配金额（万元）
    var STKNUM: Int = 0        //已获配新股数
    var sharesUnIPO: SharesUnIPO = SharesUnIPO()//未上市新股数据

    var earningTime = 12 //近一段时间 /月。默认一年
    var earningRateByTime = 0f//近一段时间 已获配金额占当期规模比例
}

class SharesUnIPO {
    //新股相关
    var SUMPLACE: Float = 0f       //已获配未上市新股金额（万元）
    var STKNUM: Int = 0        //已获配未上市新股数
}

//夏普比 标准差 信息比率 跟踪误差 同类平均跟踪误差
class SpeacialValue(var name: String?) {
    var oneYear: Float? = null
    var twoYear: Float? = null
    var threeYear: Float? = null
}

//基金净值
class NetWorth {
    var date: Date? = null//净值日期
    var AcValue: Float = 0f//累计净值
    var unitValue: Float = 0f;//单位净值
}