package load

val keyWords = arrayOf(
        "中证500",
        "中证红利",
        "中证银行",
        "基本面50",
        "300价值",
        "沪深300",
        "中证180",
        "中证800",
        "上证50",
        "中证医疗",
        "",
)

//当前策略
object ChooseTypeContants {
//    var ChooseData = fundAll
            var ChooseData = newShare
//    var ChooseData = fund500
}

//指数 筛选策略
private val fundAll = ChooseData(ChooseType.NEW_SHARE, FundSort.UNIPO_EndNAV_RATE,
        0.2f, 150f, null, null, 0f).let {
    it.fuzzyName = arrayOf(
//            "中证500",
            "中证红利",
//            "中证银行",
//            "基本面50",
//            "300价值",
//            "沪深300",
//            "中证180",
//            "中证800",
//            "上证50",
//            "中证医疗",
//            "",
    )
    it.earningTime = 12//月
//    it.srOneYear = 2f
//    it.srTwoYear = 2f
//    it.srThreeYear = 1.9f

//    it.infoROneYear = 0.2f
//    it.infoRTwoYear = 0.2f
//    it.infoRThreeYear = 0.2f
    return@let it
}

//中证500 筛选策略
private val fund500 = ChooseData(ChooseType.FUND_500, FundSort.earningRateByTime, 0.3f, 25f, null, null, 1.5f).let {
    it.fuzzyName = arrayOf("中证500")

    it.srOneYear = 1.7f
//    it.srTwoYear = 2.4f
//    it.srThreeYear = 1.9f

    it.infoROneYear = 0.3f
//    it.infoRTwoYear = 0.3f
//    it.infoRThreeYear = 0.3f
    return@let it
}

//打新基金 筛选策略
private val newShare = ChooseData(ChooseType.NEW_SHARE, FundSort.UNIPO_EndNAV_RATE, 1f, 25f, null, 25f, 4f).let {
    it.srOneYear = 2.8f
    it.srTwoYear = 2.4f
    it.srThreeYear = 1.9f

    it.sdOneYear = 4f
    it.sdTwoYear = 6f
    it.sdThreeYear = 6.5f
    return@let it
}

class ChooseData(
        var chooseType: ChooseType,
        var fundSort: FundSort,
        var minEndNav: Float,//最小规模
        var maxEndNav: Float,//最大规模
        var minShareRatio: Float?,//min 股票占比
        var maxShareRatio: Float?,//max 股票占比
        var year: Float//成立时间
) {
    //标准差
    var sdOneYear: Float? = null
    var sdTwoYear: Float? = null
    var sdThreeYear: Float? = null
    var fuzzyName: Array<String>? = null//分析基金的 模糊筛选名字。如：中证500

    //夏普比
    var srOneYear: Float? = null
    var srTwoYear: Float? = null
    var srThreeYear: Float? = null

    //信息比率
    var infoROneYear: Float? = null
    var infoRTwoYear: Float? = null
    var infoRThreeYear: Float? = null

    //跟踪误差
    var travErrorR: Float? = null

    //获配新股 时间段 /月 ，默认1年
    var earningTime: Int = 12
}

//筛选基金的策略
enum class ChooseType {
    FUND_500,
    NEW_SHARE
}

//筛选基金的策略
enum class FundSort {
    earningRateByTime,//一年收益率
    UNIPO_EndNAV_RATE,//未上市 / 基金规模
    UNIPO_ShareNav_RATE,//未上市 / 股票规模
}