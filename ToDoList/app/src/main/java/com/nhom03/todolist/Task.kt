package com.nhom03.todolist

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val dueTime: LocalTime?,
    val isDone: Boolean = false,
)

enum class StatusFilter(val label: String) {
    ALL("Tất cả"), OPEN("Chưa xong"), DONE("Đã xong")
}

enum class DateFilter(val label: String) {
    ALL("Mọi ngày"), TODAY("Hôm nay"), TOMORROW("Ngày mai"),
    UPCOMING("Sắp tới"), PAST("Đã qua");

    fun matches(date: LocalDate, today: LocalDate): Boolean = when (this) {
        ALL -> true
        TODAY -> date == today
        TOMORROW -> date == today.plusDays(1)
        UPCOMING -> date.isAfter(today)
        PAST -> date.isBefore(today)
    }
}

private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("vi-VN"))

fun LocalDate.display(): String = format(dateFormat)
fun LocalTime.display(): String = format(timeFormat)
