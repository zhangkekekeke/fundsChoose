package load.bean

import java.util.*

class Share {
    var FCODE: String = ""  //获配基金代码
    var STKCODE: String = "" //股票代码
    var STKNAME: String = "" //名称
    var PLACEDATE: String? = null //获配日期
    var LISTDATE: String? = null //上市日期
    var ISSUEPRICE :Float?= null //发行价格
    var LISTPRICE: String = "" //首日收盘价
    var SHAREPLACE: Int = 0 //获配股数
    var SUMPLACE :Float= 0.0f      //获配金额
    var NEWPRICE: String? = null
    var PCTNAV:String = ""        //获配占净资产比例

}