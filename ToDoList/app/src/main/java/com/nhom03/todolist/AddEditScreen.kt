package com.nhom03.todolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

private enum class Picker { DATE, TIME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    task: Task?,
    editing: Boolean,
    onBack: () -> Unit,
    onSave: (String, String, LocalDate?, LocalTime?, Boolean) -> String?,
) {
    var title by rememberSaveable { mutableStateOf(task?.title.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(task?.description.orEmpty()) }
    var dateText by rememberSaveable { mutableStateOf(task?.dueDate?.toString()) }
    var timeText by rememberSaveable { mutableStateOf(task?.dueTime?.toString()) }
    var isDone by rememberSaveable { mutableStateOf(task?.isDone ?: false) }
    var picker by rememberSaveable { mutableStateOf<Picker?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val date = dateText?.let(LocalDate::parse)
    val time = timeText?.let(LocalTime::parse)
    val colors = MaterialTheme.colorScheme
    val focus = LocalFocusManager.current

    Scaffold(
        containerColor = colors.background,
        topBar = { TodoTopBar(if (editing) "Sửa công việc" else "Thêm công việc", onBack) },
    ) { insets ->
        Column(
            Modifier.fillMaxSize().padding(insets).imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (editing && task == null) {
                Text("Công việc này không còn tồn tại.")
                return@Column
            }
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    error = null
                },
                label = { Text("Tên công việc (bắt buộc)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 3,
                maxLines = 6,
            )
            Surface(shape = RoundedCornerShape(20.dp), color = colors.surface) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Lịch thực hiện", fontWeight = FontWeight.SemiBold)
                    OutlinedButton(
                        onClick = {
                            focus.clearFocus()
                            picker = Picker.DATE
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(date?.let { "Ngày thực hiện · ${it.display()}" } ?: "Chọn ngày (bắt buộc)") }
                    OutlinedButton(
                        onClick = {
                            focus.clearFocus()
                            picker = Picker.TIME
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(time?.let { "Giờ thực hiện · ${it.display()}" } ?: "Chọn giờ (tùy chọn)") }
                    if (time != null) {
                        TextButton(onClick = {
                            timeText = null
                            error = null
                        }) { Text("Bỏ giờ") }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .toggleable(value = isDone, role = Role.Checkbox, onValueChange = { isDone = it })
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Checkbox(checked = isDone, onCheckedChange = null)
                Text("Đã hoàn thành")
            }
            error?.let {
                Text(
                    it,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    color = colors.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = {
                    focus.clearFocus()
                    error = onSave(title, description, date, time, isDone)
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Lưu") }
        }
    }

    when (picker) {
        Picker.DATE -> {
            val state = rememberDatePickerState(
                initialSelectedDateMillis = date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
            )
            DatePickerDialog(
                onDismissRequest = { picker = null },
                confirmButton = {
                    TextButton(
                        enabled = state.selectedDateMillis != null,
                        onClick = {
                            dateText = state.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC).toLocalDate().toString()
                            }
                            error = null
                            picker = null
                        },
                    ) { Text("Chọn") }
                },
                dismissButton = { TextButton(onClick = { picker = null }) { Text("Hủy") } },
            ) {
                DatePicker(
                    state = state,
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    title = null,
                    headline = null,
                )
            }
        }
        Picker.TIME -> {
            val initial = time ?: LocalTime.now().plusHours(1).withMinute(0)
            val state = rememberTimePickerState(initial.hour, initial.minute, is24Hour = true)
            val windowHeight = LocalWindowInfo.current.containerSize.height
            val compact = with(LocalDensity.current) { windowHeight.toDp() < 520.dp }
            var useInput by rememberSaveable { mutableStateOf(compact) }
            AlertDialog(
                onDismissRequest = { picker = null },
                title = { Text("Chọn giờ thực hiện") },
                text = {
                    Column(
                        Modifier.verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (useInput || compact) TimeInput(state) else TimePicker(state)
                        if (!compact) {
                            TextButton(onClick = { useInput = !useInput }) {
                                Text(if (useInput) "Dùng đồng hồ" else "Nhập bằng bàn phím")
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            timeText = LocalTime.of(state.hour, state.minute).toString()
                            error = null
                            picker = null
                        },
                    ) { Text("Chọn") }
                },
                dismissButton = { TextButton(onClick = { picker = null }) { Text("Hủy") } },
            )
        }
        null -> Unit
    }
}
