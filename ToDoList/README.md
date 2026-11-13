# To-Do List Nhóm 3

Ứng dụng Android cho học phần CT274, viết bằng Kotlin, Jetpack Compose và Material 3. Ba màn hình dùng chung `TaskViewModel` để quản lý danh sách và giữ dữ liệu khi xoay màn hình.

## Chạy ứng dụng

1. Trong Android Studio, chọn **File → Open** và mở thư mục dự án **D:\ToDoList_Nhom03** chứa settings.gradle.kts.
2. Chờ Gradle Sync. Dự án dùng Android Gradle Plugin 9.4.1, JDK đi kèm Android Studio và Android SDK 37.
3. Chọn cấu hình **app**, chọn **Medium Phone** hoặc điện thoại Android 8.0 trở lên, rồi nhấn **Run**. Có thể chạy .\gradlew.bat :app:assembleDebug trong PowerShell tại thư mục dự án để tạo APK app/build/outputs/apk/debug/app-debug.apk.

## Chức năng

- Xem công việc, đánh dấu hoàn thành và lọc đồng thời theo trạng thái và ngày.
- Mở chi tiết để xem mô tả, lịch thực hiện, sửa hoặc xóa sau khi xác nhận.
- Khi thêm công việc, nhập tên và chọn ngày. Mô tả và giờ có thể để trống.
- Khi lưu, ứng dụng chặn ngày giờ đã qua hoặc lịch trùng với công việc khác. Có thể sửa nội dung của một công việc cũ khi giữ nguyên lịch đã lưu.
- Danh sách khởi tạo có dữ liệu mẫu. Các thay đổi được giữ trong bộ nhớ và trở về dữ liệu mẫu khi tiến trình ứng dụng khởi động lại.

## Bàn giao theo màn hình

### Người 1 — Danh sách công việc

[ListScreen.kt](app/src/main/java/com/nhom03/todolist/ListScreen.kt) là màn hình mở đầu. Người dùng xem tiến độ, lọc công việc, đánh dấu hoàn thành, mở chi tiết hoặc thêm công việc.

| Hàm | Nhiệm vụ |
| --- | --- |
| `ListScreen()` | Nhận `tasks`, `statusFilter` và `dateFilter`. Áp dụng cả hai bộ lọc rồi sắp xếp `visibleTasks`. Nút **Thêm công việc** gọi `onAdd()`. |
| `FilterRow()` | Hiển thị hàng `FilterChip` cuộn ngang và gọi `onSelect()` với giá trị được chọn. |
| `ProgressCard()` | Tính phần trăm hoàn thành trên toàn bộ danh sách và vẽ vòng tiến độ bằng `Canvas`. |
| `TaskCard()` | Hiển thị tên, lịch và checkbox. Bấm thẻ gọi `onOpen()`. Bấm checkbox gọi `onToggle()`, dẫn đến `TaskViewModel.toggle(id)`. |

**Thư viện sử dụng** gồm Compose Foundation cho `LazyColumn`, cuộn ngang và `Canvas`, cùng Material 3 cho thẻ, checkbox, bộ lọc và nút thêm. Bộ lọc ngày dùng `DateFilter.matches()` trong [Task.kt](app/src/main/java/com/nhom03/todolist/Task.kt).

**Demo** — Chọn **Chưa xong** và **Hôm nay**, đánh dấu một việc đã hoàn thành rồi quan sát mục đó rời khỏi kết quả lọc.

### Người 2 — Chi tiết công việc

[DetailScreen.kt](app/src/main/java/com/nhom03/todolist/DetailScreen.kt) hiển thị tiêu đề, trạng thái, mô tả, ngày và giờ của công việc đã chọn. Màn hình có nút quay lại, sửa và xóa.

| Hàm | Nhiệm vụ |
| --- | --- |
| `DetailScreen()` | Nhận `task` từ `AppNavigation()`. Nút **Sửa** gọi `onEdit()`. Nút **Xóa** đặt `confirmDelete` để mở hộp thoại. Khi xác nhận, màn hình gọi `onDelete(task.id)`. |
| `DetailField()` | Hiển thị một cặp nhãn và giá trị, dùng lại cho mô tả, ngày và giờ. |
| `TodoTopBar()` | Thành phần dùng chung trong [TodoTheme.kt](app/src/main/java/com/nhom03/todolist/TodoTheme.kt) hiển thị tiêu đề và gọi `onBack()` khi bấm **Quay lại**. |

**Thư viện sử dụng** gồm Material 3 cho bố cục, nút và `AlertDialog`, Compose Foundation cho vùng cuộn, và Compose Runtime Saveable cho `rememberSaveable` để giữ trạng thái hộp thoại khi xoay màn hình.

**Demo** — Bấm **Xóa → Hủy** để kiểm tra mục vẫn còn. Sau đó xác nhận xóa và kiểm tra danh sách.

### Người 3 — Thêm và sửa công việc

[AddEditScreen.kt](app/src/main/java/com/nhom03/todolist/AddEditScreen.kt) dùng một biểu mẫu cho cả thêm và sửa. Khi sửa, biểu mẫu điền sẵn dữ liệu. Tên và ngày bắt buộc, còn mô tả và giờ có thể để trống.

| Hàm hoặc thành phần | Nhiệm vụ |
| --- | --- |
| `AddEditScreen()` | Dùng `editing` để xác định chế độ. Giữ `title`, `description`, `dateText`, `timeText` và `isDone` bằng `rememberSaveable`. |
| `DatePickerDialog()` và `DatePicker()` | Hiển thị lịch chọn ngày. Khi xác nhận, ngày được chuyển thành `LocalDate`. |
| `TimePicker()` và `TimeInput()` | Cho chọn giờ bằng đồng hồ hoặc nhập bằng bàn phím theo định dạng 24 giờ. Nút **Bỏ giờ** xóa giờ đã chọn. |
| `onSave(...)` | Callback của nút **Lưu**. `AppNavigation()` gọi `TaskViewModel.save()`. Biểu mẫu hiển thị thông báo khi có lỗi. Khi thành công, luồng thêm quay về danh sách và luồng sửa quay về chi tiết. |

**Thư viện sử dụng** gồm Material 3 cho ô nhập, hộp thoại và bộ chọn ngày giờ, Compose Foundation cho vùng cuộn, Compose Runtime Saveable để giữ nội dung đang nhập và API chuẩn `java.time` để xử lý ngày giờ.

**Demo** — Thêm một việc có ngày và bỏ trống giờ. Mở lại để đổi tiêu đề, sau đó thử chọn lịch đã qua để kiểm tra thông báo lỗi.

### Mã nguồn dùng chung

- [Task.kt](app/src/main/java/com/nhom03/todolist/Task.kt) khai báo `id`, `title`, `description`, `dueDate`, `dueTime` và `isDone`.
- [TaskViewModel.kt](app/src/main/java/com/nhom03/todolist/TaskViewModel.kt) dùng AndroidX Lifecycle ViewModel và trạng thái Compose. `save()` thêm khi `id` là `null` và cập nhật theo `id` khi sửa. Hàm kiểm tra tên, ngày, lịch đã qua và công việc khác trùng cả ngày lẫn giờ. Khi sửa, có thể giữ nguyên lịch cũ đã qua. `toggle(id)` đảo trạng thái hoàn thành và `delete(id)` xóa công việc.
- [AppNavigation.kt](app/src/main/java/com/nhom03/todolist/AppNavigation.kt) dùng `screen` và `selectedId` để nối các màn hình, `rememberSaveable` để giữ trạng thái và `BackHandler` của Activity Compose để xử lý nút quay lại.
