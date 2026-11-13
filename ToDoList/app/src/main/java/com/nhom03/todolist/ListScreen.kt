package com.nhom03.todolist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ListScreen(
    tasks: List<Task>,
    listState: LazyListState,
    statusFilter: StatusFilter,
    dateFilter: DateFilter,
    onStatusFilter: (StatusFilter) -> Unit,
    onDateFilter: (DateFilter) -> Unit,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    onToggle: (Long) -> Unit,
) {
    val today = LocalDate.now()
    val visibleTasks = tasks.filter { task ->
        dateFilter.matches(task.dueDate, today) && when (statusFilter) {
            StatusFilter.ALL -> true
            StatusFilter.OPEN -> !task.isDone
            StatusFilter.DONE -> task.isDone
        }
    }.sortedWith(
        compareBy<Task> { it.dueDate.isBefore(today) }
            .thenBy { it.dueDate }
            .thenBy { it.dueTime ?: LocalTime.MAX }
    )
    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                icon = {
                    Canvas(Modifier.size(20.dp)) {
                        val weight = 2.dp.toPx()
                        drawLine(colors.onPrimary, Offset(0f, center.y), Offset(size.width, center.y), weight, StrokeCap.Round)
                        drawLine(colors.onPrimary, Offset(center.x, 0f), Offset(center.x, size.height), weight, StrokeCap.Round)
                    }
                },
                text = { Text("Thêm công việc") },
            )
        },
    ) { insets ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(insets),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(today.display(), style = MaterialTheme.typography.labelLarge, color = colors.primary)
                    Text("Danh sách công việc", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }
            }
            item {
                ProgressCard(completed = tasks.count { it.isDone }, total = tasks.size)
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    FilterRow(StatusFilter.entries, statusFilter, { it.label }, onStatusFilter)
                    FilterRow(DateFilter.entries, dateFilter, { it.label }, onDateFilter)
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Công việc", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("${visibleTasks.size} mục", style = MaterialTheme.typography.labelLarge, color = colors.onSurfaceVariant)
                }
            }
            if (visibleTasks.isEmpty()) {
                item {
                    Surface(shape = RoundedCornerShape(20.dp), color = colors.surfaceVariant) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Chưa có công việc", fontWeight = FontWeight.SemiBold)
                            Text(
                                if (tasks.isEmpty()) "Nhấn Thêm công việc để bắt đầu."
                                else "Thử chọn bộ lọc khác để xem công việc.",
                                color = colors.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            items(visibleTasks, key = { it.id }) { task ->
                TaskCard(task, today, onOpen = { onOpen(task.id) }, onToggle = { onToggle(task.id) })
            }
        }
    }
}

@Composable
private fun <T> FilterRow(options: List<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (option in options) {
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(label(option)) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.primaryContainer,
                    selectedLabelColor = colors.onPrimaryContainer,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true, selected = selected == option, borderColor = colors.outlineVariant,
                ),
            )
        }
    }
}

@Composable
private fun ProgressCard(completed: Int, total: Int) {
    val white = Color.White
    val percentage = if (total == 0) 0 else completed * 100 / total
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF433184),
        contentColor = white,
    ) {
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text("Tiến độ", style = MaterialTheme.typography.labelLarge, color = Color(0xFFE6DBFF))
                Spacer(Modifier.height(4.dp))
                Text("$completed/$total", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("công việc đã xong", style = MaterialTheme.typography.bodyMedium)
            }
            Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val stroke = 5.dp.toPx()
                    val inset = stroke / 2
                    drawCircle(white.copy(alpha = .24f), radius = center.x - inset, style = Stroke(stroke))
                    if (percentage > 0) drawArc(
                        color = Color(0xFFDDD1FF),
                        startAngle = -90f,
                        sweepAngle = 360f * completed / total,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - stroke, size.height - stroke),
                        style = Stroke(stroke, cap = StrokeCap.Round),
                    )
                }
                Text("$percentage%", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TaskCard(task: Task, today: LocalDate, onOpen: () -> Unit, onToggle: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val day = when (task.dueDate) {
        today -> "Hôm nay"
        today.plusDays(1) -> "Ngày mai"
        else -> task.dueDate.display()
    }
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
    ) {
        Row(
            Modifier.padding(start = 5.dp, end = 15.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() },
                modifier = Modifier.semantics { contentDescription = "Hoàn thành: ${task.title}" },
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
                Text(
                    task.dueTime?.let { "$day · ${it.display()}" } ?: day,
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Canvas(Modifier.size(14.dp)) {
                val width = 1.6.dp.toPx()
                val tip = Offset(size.width * .68f, center.y)
                drawLine(colors.outline, Offset(size.width * .32f, size.height * .18f), tip, width, StrokeCap.Round)
                drawLine(colors.outline, tip, Offset(size.width * .32f, size.height * .82f), width, StrokeCap.Round)
            }
        }
    }
}
