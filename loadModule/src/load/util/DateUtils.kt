package com.common.lib.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getQuot(date1: Date, date2: Date): Long {
        var quot: Long = 0
        try {
            quot = date1.getTime() - date2.getTime()
            quot = quot / 1000 / 60 / 60 / 24
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return quot
    }

    /**
     * 判断今日新债
     * 数据时间是当日：00：00
     * 传入当前时间，判断00：00 - 15:00 之间的新债
     */
    fun isCurBond(startDate: Date): Boolean {
        val endDate = Date(startDate.time + 15 * 60 * 60 * 1000)
        val cur = Date()
        return cur.after(startDate) && cur.before(endDate)
    }
    /**
     * 已过申购时间转债
     * 数据时间是当日：00：00
     * 传入当前时间，判断00：00 - 15:00 之间的新债
     */
    fun isOldBond(startDate: Date): Boolean {
        val endDate = Date(startDate.time + 15 * 60 * 60 * 1000)
        val cur = Date()
        return cur.after(endDate)
    }
}