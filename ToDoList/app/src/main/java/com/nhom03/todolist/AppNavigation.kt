package com.nhom03.todolist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

private enum class Screen { LIST, DETAIL, ADD, EDIT }

@Composable
fun AppNavigation(viewModel: TaskViewModel) {
    var screen by rememberSaveable { mutableStateOf(Screen.LIST) }
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    var statusFilter by rememberSaveable { mutableStateOf(StatusFilter.ALL) }
    var dateFilter by rememberSaveable { mutableStateOf(DateFilter.ALL) }
    val listState = rememberLazyListState()
    val task = viewModel.tasks.firstOrNull { it.id == selectedId }
    val back: () -> Unit = {
        screen = if (screen == Screen.EDIT && task != null) Screen.DETAIL else Screen.LIST
    }

    BackHandler(enabled = screen != Screen.LIST, onBack = back)

    when (screen) {
        Screen.LIST -> ListScreen(
            tasks = viewModel.tasks,
            listState = listState,
            statusFilter = statusFilter,
            dateFilter = dateFilter,
            onStatusFilter = { statusFilter = it },
            onDateFilter = { dateFilter = it },
            onAdd = { screen = Screen.ADD },
            onOpen = {
                selectedId = it
                screen = Screen.DETAIL
            },
            onToggle = viewModel::toggle,
        )
        Screen.DETAIL -> DetailScreen(
            task = task,
            onBack = back,
            onEdit = { screen = Screen.EDIT },
            onDelete = {
                viewModel.delete(it)
                screen = Screen.LIST
            },
        )
        Screen.ADD, Screen.EDIT -> AddEditScreen(
            task = if (screen == Screen.EDIT) task else null,
            editing = screen == Screen.EDIT,
            onBack = back,
            onSave = { title, description, date, time, isDone ->
                val error = viewModel.save(
                    if (screen == Screen.EDIT) selectedId else null,
                    title, description, date, time, isDone,
                )
                if (error == null) {
                    if (screen == Screen.ADD) {
                        statusFilter = StatusFilter.ALL
                        dateFilter = DateFilter.ALL
                    }
                    back()
                }
                error
            },
        )
    }
}
