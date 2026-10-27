# 🍎 macOs Launcher for Android

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203%20%2B%20macOS-000000?style=for-the-badge&logo=apple&logoColor=white" alt="macOS Design" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License" />
</p>

---

## 📖 Giới thiệu (Overview)

**macOs Launcher** là ứng dụng trình khởi chạy (Launcher) Android mang phong cách giao diện máy tính **macOS (Sonoma / Sequoia)** hiện đại, sang trọng và tối giản. Được xây dựng 100% bằng **Kotlin** và **Jetpack Compose**, mang lại hiệu ứng mượt mà, hỗ trợ các thao tác cử chỉ trực quan, tích hợp thanh Dock bo góc trong suốt, Launchpad toàn màn hình, cùng tiện ích theo dõi **Giá Cà Phê** và **Giá Vàng Nhẫn** thời gian thực tại Việt Nam.

> **Tác giả:** Dev Trường Thanh  
> **Facebook:** [fb.com/TruongAlone](https://facebook.com/TruongAlone)  
> **Tên ứng dụng:** macOs  

---

## ✨ Tính năng nổi bật (Key Features)

### 1. 🖥️ Giao diện Desktop macOS Tối Giản & Thanh Lịch
- **Màn hình chính sạch sẽ:** Đã lược bỏ thanh menu trên cùng (Top Menu Bar) và Control Center rườm rà, tạo không gian hiển thị rộng rãi, chuẩn phong cách màn hình nền desktop macOS.
- **Hình nền macOS & Tùy chọn ảnh trên máy:** Tích hợp bộ sưu tập hình nền chất lượng cao phong cách macOS (Sonoma, Sequoia, Ventura, Dark Mode...) và cho phép tự do chọn hình ảnh bất kỳ từ thư viện ảnh của thiết bị.

### 2. 🚢 Thanh Dock 4 Ứng Dụng Chuẩn macOS
- Thiết kế thanh Dock nổi bo góc với hiệu ứng kính mờ trong suốt (Frosted Glass / Blur).
- Cố định 4 vị trí ứng dụng thường dùng nhất.
- Bỏ các dấu chấm chấm dưới thanh Dock cho giao diện liền mạch, gọn gàng.
- Cho phép người dùng tùy ý thay đổi 4 ứng dụng trong Dock qua Cài đặt hoặc nhấn giữ.

### 3. 🚀 Launchpad (Tất cả ứng dụng)
- Mở danh sách ứng dụng toàn màn hình theo dạng lưới phân trang mượt mà như macOS Launchpad.
- **Tìm kiếm thông minh:** Thanh tìm kiếm ứng dụng tức thì kèm nút **Tìm kiếm bằng giọng nói (Microphone)** tiện lợi.
- **Rung phản hồi (Haptic Feedback) & Menu thao tác khi nhấn giữ icon:**
  - 🔄 **Sắp xếp vị trí:** Di chuyển vị trí giữa các ứng dụng trong Launchpad (Dời tiến/lùi hoặc hoán đổi với ứng dụng khác).
  - 🗑️ **Gỡ cài đặt (Xóa):** Mở hộp thoại gỡ cài đặt hệ thống nhanh chóng.
  - 👁️ **Ẩn ứng dụng:** Ẩn các ứng dụng riêng tư khỏi danh sách.

### 4. 📈 Widget Giá Cà Phê & Giá Vàng Thời Gian Thực
- **Nằm đối xứng với đồng hồ và lịch âm** trên màn hình chính:
  - ☕ **Giá Cà Phê Nội Địa:** Lấy dữ liệu trực tiếp từ liên kết *https://giacaphe.com/gia-ca-phe-noi-dia/* cho các tỉnh: **Đắk Lắk, Lâm Đồng, Gia Lai, Đắk Nông**.
  - 🌐 **Hộp thoại xem Giá Cà Trực Tuyến:** Chạm vào widget để mở hộp thoại hiển thị trực tiếp trang *https://giacaphe.com/gia-ca-phe-truc-tuyen/* với biểu đồ và bảng giá chi tiết.
  - 🪙 **Giá Vàng:** Cập nhật giá mua vào / bán ra chuẩn thị trường: **Vàng miếng SJC, Vàng Nhẫn 9999 SJC, DOJI, PNJ**.
  - 🔄 Nút làm mới dữ liệu nhanh (Refresh) và tự động cập nhật.
  - ⚙️ Tùy chọn bật/tắt hiển thị trong Cài đặt hệ thống.

### 5. ⏰ Đồng Hồ Số & Lịch Vạn Niên / Âm Lịch Việt Nam
- Hiển thị ngày tháng, giờ phút chuẩn xác.
- Tích hợp thuật toán chuyển đổi **Âm Lịch Việt Nam**, hiển thị ngày tháng âm lịch, năm **Can Chi** và giờ **Hoàng Đạo**.
- Tùy chọn bật/tắt hiển thị linh hoạt trong Cài đặt.

### 6. 👆 Cử Chỉ Thông Minh (Gestures)
Mọi thao tác cử chỉ đều có thể tùy chỉnh bật hoặc tắt trong Cài đặt:
- 👉 **Trượt sang phải (Swipe Right):** Mở nhanh Launchpad / Tất cả ứng dụng.
- ☝️ **Trượt lên (Swipe Up):** Mở danh sách ứng dụng / Launchpad.
- 👇 **Trượt xuống (Swipe Down):** Mở thanh thông báo hệ thống (Notification Panel) ở bất kỳ đâu trên màn hình chính.
- 💡 **Thanh hướng dẫn thao tác:** Gợi ý vuốt trên màn hình chính, có thể tắt để màn hình luôn gọn gàng.

### 7. ⚙️ Tùy Chọn Cài Đặt Phong Cách macOS System Settings
- Hộp thoại Cài đặt trực quan với thanh Sidebar bên trái và nội dung bên phải:
  - 🖼️ **Hình nền (Wallpapers):** Đổi hình nền macOS hoặc ảnh cá nhân từ máy.
  - 🚢 **Thanh Dock:** Chọn và sắp xếp lại 4 ứng dụng ghim vào thanh Dock.
  - ⏰ **Đồng hồ & Lịch âm:** Bật / tắt hiển thị giờ và âm lịch.
  - ☕ **Giá Cà Phê & Vàng:** Bật / tắt widget thị trường.
  - 👆 **Cử chỉ & Thao tác:** Bật / tắt cử chỉ vuốt phải, vuốt lên, vuốt xuống và thanh hướng dẫn.
  - 📱 **Đặt làm Mặc Định:** Mở cài đặt Home app mặc định của Android.
  - ℹ️ **Giới thiệu:** Thông tin ứng dụng và liên hệ nhà phát triển **Dev Trường Thanh**.

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

- **Ngôn ngữ:** Kotlin 100%
- **Giao diện:** Jetpack Compose, Material 3
- **Kiến trúc:** MVVM (Model - View - ViewModel) + Coroutines & StateFlow
- **Thư viện chính:**
  - `androidx.compose.foundation` & `androidx.compose.material3`
  - `androidx.compose.foundation.pager` (HorizontalPager)
  - `androidx.lifecycle:lifecycle-viewmodel-compose`
  - `coil-compose` (Xử lý hình ảnh và wallpaper)
  - `com.squareup.retrofit2` & `gson` (Kết nối API giá cả)
  - `androidx.activity.compose` & `BackHandler`
- **Tự động hóa CI/CD:** GitHub Actions (Build APK tự động khi push code hoặc tạo tag).

---

## 🚀 Hướng Dẫn Tải & Cài Đặt (Download & Install)

### Cách 1: Tải file APK từ GitHub Releases (Khuyến nghị)
1. Vào tab **[Releases](../../releases)** của kho lưu trữ GitHub này.
2. Tìm phiên bản mới nhất và tải file `macOS-Launcher-debug.apk`.
3. Mở file APK trên điện thoại Android và chọn **Cài đặt** (Cho phép cài đặt từ nguồn không xác định nếu được yêu cầu).
4. Nhấn nút Home và chọn **macOs** làm Trình khởi chạy mặc định.

### Cách 2: Tải từ GitHub Actions Artifacts
1. Vào mục **Actions** trên GitHub.
2. Chọn lần chạy gần nhất của workflow **Build and Release Android APK**.
3. Cuộn xuống phần **Artifacts** và tải về gói `macOS-Launcher-APK`.

---

## 💻 Hướng Dẫn Tự Build Mã Nguồn (Build from Source)

### Yêu cầu môi trường:
- **Android Studio:** Hedgehog | 2023.1.1 trở lên
- **JDK:** Java 17
- **Android SDK:** Compile SDK 35, Min SDK 26, Target SDK 35

### Các bước thực hiện:

```bash
# 1. Clone repository về máy tính
git clone https://github.com/<your-username>/Apple-launcher-android.git
cd Apple-launcher-android

# 2. Cấp quyền thực thi cho Gradle (Linux / macOS)
chmod +x gradlew

# 3. Build file APK Debug
./gradlew assembleDebug

# 4. File APK sau khi build thành công sẽ nằm tại:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🤖 Tự Động Build Bằng GitHub Actions

Dự án đã được cấu hình sẵn file workflow `.github/workflows/build-apk.yml`:
- Tự động build file APK khi bạn push mã nguồn lên nhánh `main` hoặc `master`.
- Tự động tạo bản **GitHub Release** khi bạn tạo và push git tag (ví dụ: `v1.0.0`).
- Cho phép kích hoạt build thủ công trực tiếp bằng nút **"Run workflow"** trên GitHub.

---

## 👨‍💻 Thông Tin Tác Giả (Author)

- **Nhà phát triển:** Dev Trường Thanh
- **Facebook:** [https://facebook.com/TruongAlone](https://facebook.com/TruongAlone)
- **Hỗ trợ & Đóng góp:** Mọi góp ý, báo lỗi (issue) hoặc yêu cầu tính năng mới xin vui lòng mở Issue hoặc gửi Pull Request trên GitHub.

---

## 📄 Bản Quyền (License)

Dự án được phân phối dưới giấy phép **MIT License**. Bạn được tự do sử dụng, chỉnh sửa và phát triển tiếp vì mục đích học tập và cộng đồng.
