package com.udacity.asteroidradar.util

import java.text.SimpleDateFormat
import java.util.*

object Utils {


    fun getFormattedDate( pattern:String, date:Date):String{
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(date)
        return date
    }
    fun getWeekBackFromDate(date:Date):Date{
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        return calendar.time
    }
    fun getFormattedWeekBackFromDate(pattern:String, date:Date):String{

        return getFormattedDate(pattern, getWeekBackFromDate(date))
    }
}