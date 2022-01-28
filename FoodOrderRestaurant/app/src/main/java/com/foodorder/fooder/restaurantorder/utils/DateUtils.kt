package com.foodorder.fooder.restaurantorder.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    const val DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm"
    const val TIME_DATE_FORMAT = "HH:mm MM/dd/yyyy"
    const val DATE_FORMAT1 = "MM/dd/yyyy"
    const val DATE_FORMAT2 = "dd/MM/yyyy"
    const val DATE_FORMAT3 = "dd/MM"
    const val TIME_FORMAT = "HH:mm"

    fun convertLongToDateTime(value: Long?, format: String? = DATE_TIME_FORMAT): String {
        if (value == null) return ""
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
        val date = Date(value)
        return dateFormatter.format(date)
    }

    fun currentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun current(): Calendar {
        return Calendar.getInstance()
    }

    fun convertLongToDate(value: Long?): String {
        if (value == null) return ""
        val dateFormatter = SimpleDateFormat(DATE_FORMAT1, Locale.getDefault())
        val date = Date(value)
        return dateFormatter.format(date)
    }

    fun convertLongToTime(value: Long?): String {
        if (value == null) return ""
        val dateFormatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        val date = Date(value)
        return dateFormatter.format(date)
    }

    fun combineDateTime(date: Date, time: Date): Long {
        val c = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date
        val timeCalendar = Calendar.getInstance()
        timeCalendar.time = time
        val year = dateCalendar.get(Calendar.YEAR)
        val month = dateCalendar.get(Calendar.MONTH)
        val day = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = timeCalendar.get(Calendar.HOUR_OF_DAY)
        val minutes = timeCalendar.get(Calendar.MINUTE)
        c.set(year, month, day, hour, minutes)
        return c.time.time
    }

    fun convertDatetoString(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT2).format(date)
    }

    fun convertCalendartoString(calendar: Calendar): String {
        return SimpleDateFormat(DATE_FORMAT2).format(calendar.time)
    }

    fun convertCalendartoDayMonth(calendar: Calendar): String {
        return SimpleDateFormat(DATE_FORMAT3).format(calendar.time)
    }

    fun convertStringToTime(date: String): Calendar {
        val arr = date.split("/")

        val calendar = current()

        calendar.set(arr[2].toInt(), arr[1].toInt(), arr[0].toInt(), 0, 0, 0)

        return calendar
    }
}