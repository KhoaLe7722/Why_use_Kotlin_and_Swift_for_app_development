package com.nhom03.todolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailScreen(task: Task?, onBack: () -> Unit, onEdit: () -> Unit, onDelete: (Long) -> Unit) {
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    Scaffold(
        containerColor = colors.background,
        topBar = { TodoTopBar("Chi tiết công việc", onBack) },
    ) { insets ->
        Column(
            Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (task == null) {
                Text("Công việc này không còn tồn tại.")
                return@Column
            }
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = colors.primaryContainer,
                contentColor = colors.onPrimaryContainer,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (task.isDone) "Đã hoàn thành" else "Chưa hoàn thành", style = MaterialTheme.typography.labelLarge)
                    Text(task.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
            Surface(shape = RoundedCornerShape(20.dp), color = colors.surface) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DetailField("Mô tả", task.description.ifBlank { "Chưa có mô tả" })
                    DetailField("Ngày thực hiện", task.dueDate.display())
                    DetailField("Giờ thực hiện", task.dueTime?.display() ?: "Chưa đặt giờ")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { confirmDelete = true }, modifier = Modifier.weight(1f)) {
                    Text("Xóa", color = colors.error)
                }
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Sửa") }
            }
        }
    }
    if (confirmDelete && task != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Xóa công việc?") },
            text = { Text("Bạn có muốn xóa “${task.title}” khỏi danh sách?") },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Hủy") } },
            confirmButton = {
                TextButton(onClick = { onDelete(task.id) }) { Text("Xóa", color = colors.error) }
            },
        )
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
