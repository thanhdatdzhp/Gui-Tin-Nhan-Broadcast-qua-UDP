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

- **Server:** lắng nghe gói tin được gửi đến trên một cổng UDP cố định, hiển thị nội dung tin nhắn lên giao diện và có thể phản hồi tới toàn bộ các client trong mạng.  
- **Client:** cung cấp giao diện cho người dùng nhập nội dung tin nhắn, sau đó gửi tin đó đến địa chỉ broadcast. Tất cả client khác trong mạng nhận được gói tin ngay lập tức.  

### 🔑 Nguyên lý hoạt động
1. Client tạo một **DatagramPacket** chứa nội dung tin nhắn.
2. Gói tin được gửi đến địa chỉ broadcast (ví dụ: `192.168.1.255`).
3. Server và các client khác đang lắng nghe trên cổng UDP sẽ nhận được gói tin.
4. Tin nhắn được hiển thị đồng thời trên nhiều máy, mô phỏng cơ chế truyền tin nhanh trong LAN.  

Hệ thống khai thác ưu điểm của UDP:  
- Không cần kết nối (connectionless) → **giảm độ trễ**.  
- Cấu trúc gói tin đơn giản → **xử lý nhanh**.  
- Dễ mở rộng cho các bài toán như: chat nội bộ, gửi thông báo, quản lý hệ thống.  

---

## 🌟 Ưu điểm nổi bật

- ✅ **Dễ triển khai:** chỉ cần chạy server và client trên cùng mạng LAN.  
- ⚡ **Tốc độ nhanh:** nhờ UDP không yêu cầu handshake.  
- 📚 **Minh họa kiến thức thực tiễn:** phù hợp cho sinh viên học lập trình mạng, socket, giao thức truyền thông.  
- 🔧 **Ứng dụng thực tế:** có thể mở rộng thành hệ thống chat nhóm, cảnh báo trong doanh nghiệp, hoặc giám sát hệ thống.  

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
  <img width="600" height="480" src="" alt="Ảnh 1"/> 
</p>
<p align="center"><b>Ảnh 1: Giao diện Server đang chạy</b></p>

<p align="center">
  <img width="450" height="260" src="" alt="Ảnh 2"/> 
</p>
<p align="center"><b>Ảnh 2: Giao diện nhập địa chỉ IP Server</b></p>

<p align="center">
  <img width="600" height="480" src="" alt="Ảnh 3"/> 
</p>
<p align="center"><b>Ảnh 3: Giao diện Client gửi và nhận tin nhắn</b></p>

---

## 📦 4. Hướng Dẫn Cài Đặt & Sử Dụng  

### 🔹 Yêu cầu hệ thống  
- Eclipse IDE (bản mới nhất).  
- JDK 21 hoặc cao hơn.  
- Git để tải project từ GitHub.  

### 🔹 Các bước thực hiện  

#### **Bước 1: Clone project từ GitHub**
```bash
git clone https://github.com/caovan-huy/gui-tin-nhan-broadcast-qua-UDP/tree/main/Bai_Tap_Lon
