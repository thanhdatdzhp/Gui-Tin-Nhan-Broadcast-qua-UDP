<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>

<h2 align="center">
   📡 Hệ Thống Gửi Tin Nhắn Broadcast Qua UDP
</h2>

<div align="center">
    <p align="center">
        <img alt="AIoTLab Logo" width="170" src="https://github.com/user-attachments/assets/711a2cd8-7eb4-4dae-9d90-12c0a0a208a2" />
        <img alt="AIoTLab Logo" width="180" src="https://github.com/user-attachments/assets/dc2ef2b8-9a70-4cfa-9b4b-f6c2f25f1660" />
        <img alt="DaiNam University Logo" width="200" src="https://github.com/user-attachments/assets/77fe0fd1-2e55-4032-be3c-b1a705a1b574" />
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

---

## 📖 1. Giới thiệu  

Hệ thống **gửi tin nhắn broadcast qua UDP** là một ứng dụng mô phỏng việc **truyền thông điệp trong mạng cục bộ (LAN)** bằng cách sử dụng **giao thức UDP** kết hợp với **kỹ thuật broadcast**.  

Ứng dụng được xây dựng với hai thành phần chính:  

- **Server:**: đóng vai trò “điểm thu phát trung tâm”, lắng nghe các gói tin được gửi đến qua cổng UDP định trước, hiển thị nội dung tin nhắn lên giao diện và phản hồi cho tất cả các máy trong cùng mạng.  
- **Client:** cho phép người dùng nhập nội dung tin nhắn và gửi đi. Tin nhắn này sẽ được broadcast tới địa chỉ quảng bá (broadcast address) của mạng LAN, đảm bảo tất cả các thiết bị đang chạy ứng dụng đều có thể nhận được. 

### 🔑 Nguyên lý hoạt động
UDP là một giao thức không kết nối (connectionless), tốc độ xử lý nhanh, gói tin được gửi đi mà không cần quá trình “bắt tay” như TCP. Khi người dùng gửi tin nhắn:
1. Client tạo gói tin chứa dữ liệu người dùng nhập.
2. Gói tin được gửi đến địa chỉ broadcast của mạng (ví dụ: 192.168.1.255).
3. Tất cả các thiết bị trong cùng subnet đang lắng nghe trên cổng UDP đó sẽ nhận được gói tin.
4. Server nhận và hiển thị nội dung, đồng thời có thể phản hồi lại cho các client khác.
Điều này giúp hệ thống trở thành một giải pháp lý tưởng để truyền thông điệp nhanh trong mạng nội bộ.

Hệ thống khai thác ưu điểm của UDP:  
- Không cần kết nối (connectionless) → **giảm độ trễ**.  
- Cấu trúc gói tin đơn giản → **xử lý nhanh**.  
- Dễ mở rộng cho các bài toán như: chat nội bộ, gửi thông báo, quản lý hệ thống.  

---

## 🌟 Ưu điểm nổi bật

- ✅ **Đơn giản, dễ triển khai:** không yêu cầu thiết lập kết nối phức tạp.  
- ⚡ **Tốc độ truyền nhanh:**  do UDP không có cơ chế xác nhận gói tin nên độ trễ thấp, phù hợp với các ứng dụng thông báo tức thời.  
- 📚 **Minh họa kiến thức lập trình mạng:**  giúp người học hiểu rõ hơn về socket, broadcast, và cơ chế hoạt động của UDP.  
- 🔧 **Ứng dụng thực tiễn:**  có thể mở rộng để xây dựng các hệ thống chat nội bộ, công cụ gửi thông báo trong LAN, hoặc phần mềm hỗ trợ quản trị hệ thống.  

---

## 🔧 2. Ngôn ngữ và Công nghệ Sử Dụng  

| Thành phần | Mô tả |
|-----------|-------|
| **Ngôn ngữ** | [![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/) |
| **Giao diện** | Java Swing |
| **Giao thức mạng** | UDP (User Datagram Protocol) |
| **IDE phát triển** | Eclipse (khuyến nghị bản mới nhất) |
| **Môi trường chạy** | JDK 21 hoặc cao hơn |

---

## 🚀 3. Hình Ảnh Minh Họa  

<p align="center">
  <img width="600" height="480" src="https://github.com/thanhdatdzhp/Gui-Tin-Nhan-Broadcast-qua-UDP/blob/main/img/server.jpg" alt="Ảnh 1"/> 
</p>
<p align="center"><b>Ảnh 1: Bộ gửi UDP </b></p>

<p align="center">
  <img width="450" height="260" src="https://github.com/thanhdatdzhp/Gui-Tin-Nhan-Broadcast-qua-UDP/blob/main/img/nhantinnhan.jpg" alt="Ảnh 2"/> 
</p>
<p align="center"><b>Ảnh 2: Giao diện nhận tin nhắn</b></p>

<p align="center">
  <img width="450" height="260" src="https://github.com/thanhdatdzhp/Gui-Tin-Nhan-Broadcast-qua-UDP/blob/main/img/nhanfile.jpg" alt="Ảnh 3"/> 
</p>
<p align="center"><b>Ảnh 2: Giao diện nhận file âm thanh , radio</b></p>

<p align="center">
  <img width="600" height="480" src="https://github.com/thanhdatdzhp/Gui-Tin-Nhan-Broadcast-qua-UDP/blob/main/img/cvs.jpg" alt="Ảnh 4"/> 
</p>
<p align="center"><b>Ảnh 3: Xuất CSV và mở bằng Excel</b></p>

---

## 📦 4. Hướng Dẫn Cài Đặt & Sử Dụng  

### 🔹 Yêu cầu hệ thống  
- Eclipse IDE (bản mới nhất).  
- JDK 21 hoặc cao hơn.  
- Git để tải project từ GitHub.  

### 🔹 Các bước thực hiện  

#### **Bước 1: Clone project từ GitHub**
```bash
https://github.com/thanhdatdzhp/Gui-Tin-Nhan-Broadcast-qua-UDP/tree/main/TinNhanBROADCAST
```

Bước 2: Import vào Eclipse

- Mở Eclipse → File → Import → Existing Projects into Workspace.
- Chọn thư mục project vừa clone về.
- Nhấn Finish để hoàn tất.

Bước 3: Kiểm tra môi trường
- Đảm bảo project đang chạy với JavaSE-21.
- Nếu thiếu thư viện, mở Project → Properties → Java Build Path để thêm JDK tương ứng.

Bước 4: Chạy ứng dụng

- Chạy UDPSenderLTM.java trước để khởi động server.
- Chạy UDPReceiverLTM.java trên một hoặc nhiều máy để nhận tin nhắn và các tập tin khác.

Bước 5: Sử dụng

- Nhập nội dung tin nhắn → nhấn Gửi.
- Chọn 'Chọn tệp' để gửi các file.
- Các client khác trong cùng mạng LAN khi online sẽ nhận được tin nhắn và file ngay lập tức , còn các mạng Lan khi không online sẽ k nhận đc tin nhắn và file.
- Sau khi nhận được tệp file chúng ta có thể click vào file rồi nhận vào nút 'Phát' để bật file dữ liệu vừa nhận được



📱 5. Thông Tin Liên Hệ

👤 Họ và tên: Ngô Thành Đạt <br>
🏫 Lớp: CNTT 16-01 <br>
📧 Email: thanhdatdzhp11@gmail.com <br>
☎ Điện thoại: 0901 562 638 <br>

© 2025 AIoTLab, Faculty of Information Technology – DaiNam University.
