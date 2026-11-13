package com.nhom03.todolist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class TaskViewModel : ViewModel() {
    private val today = LocalDate.now()
    private var nextId = 5L

    var tasks by mutableStateOf(
        listOf(
            Task(1, "Học Jetpack Compose", "Thực hành tạo giao diện bằng Kotlin và Material 3.",
                today, null),
            Task(2, "Ôn bài lập trình di động", "Xem lại phần ViewModel và quản lý trạng thái.",
                today, null, isDone = true),
            Task(3, "Hoàn thiện bài tập CT274", "Kiểm tra các màn hình trước khi nộp bài.",
                today.plusDays(1), LocalTime.of(19, 0)),
            Task(4, "Chuẩn bị tài liệu nhóm", "Đọc báo cáo tham khảo và ghi lại yêu cầu.",
                today.minusDays(1), LocalTime.of(16, 0), isDone = true),
        )
    )
        private set

    fun toggle(id: Long) {
        tasks = tasks.map { if (it.id == id) it.copy(isDone = !it.isDone) else it }
    }

    fun delete(id: Long) {
        tasks = tasks.filterNot { it.id == id }
    }

    fun save(
        id: Long?,
        title: String,
        description: String,
        date: LocalDate?,
        time: LocalTime?,
        isDone: Boolean,
    ): String? {
        if (title.isBlank()) return "Vui lòng nhập tên công việc."
        if (date == null) return "Vui lòng chọn ngày thực hiện."
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        val current = tasks.firstOrNull { it.id == id }
        if (id != null && current == null) return "Công việc này không còn tồn tại."

        // A previously saved deadline can stay in place when editing other fields.
        if (current?.dueDate != date || current.dueTime != time) {
            if (date.isBefore(now.toLocalDate())) return "Không được chọn ngày trong quá khứ."
            if (time != null && date.atTime(time).isBefore(now)) {
                return "Không được chọn giờ trong quá khứ."
            }
        }
        if (time != null && tasks.any { it.id != id && it.dueDate == date && it.dueTime == time }) {
            return "Ngày và giờ này trùng với một công việc đã có."
        }

        val task = Task(id ?: nextId++, title.trim(), description.trim(), date, time, isDone)
        tasks = if (id == null) tasks + task else tasks.map { if (it.id == id) task else it }
        return null
    }
}
