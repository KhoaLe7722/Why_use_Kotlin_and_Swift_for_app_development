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
