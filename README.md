📖 1. Giới thiệu

- Hệ thống gửi tin nhắn broadcast qua UDP là một ứng dụng minh họa cơ chế truyền thông tin trong mạng cục bộ (LAN) bằng giao thức UDP (User Datagram Protocol) kết hợp với kỹ thuật broadcast.
- Ứng dụng được thiết kế với hai thành phần chính:
- Server: đóng vai trò “điểm thu phát trung tâm”, lắng nghe các gói tin được gửi đến qua cổng UDP định trước, hiển thị nội dung tin nhắn lên giao diện và phản hồi cho tất cả các máy trong cùng mạng.
- Client: cho phép người dùng nhập nội dung tin nhắn và gửi đi. Tin nhắn này sẽ được broadcast tới địa chỉ quảng bá (broadcast address) của mạng LAN, đảm bảo tất cả các thiết bị đang chạy ứng dụng đều có thể nhận được.

🔑 Nguyên lý hoạt động

- UDP là một giao thức không kết nối (connectionless), tốc độ xử lý nhanh, gói tin được gửi đi mà không cần quá trình “bắt tay” như TCP. Khi người dùng gửi tin nhắn:
- Client tạo gói tin chứa dữ liệu người dùng nhập.
- Gói tin được gửi đến địa chỉ broadcast của mạng (ví dụ: 192.168.1.255).
- Tất cả các thiết bị trong cùng subnet đang lắng nghe trên cổng UDP đó sẽ nhận được gói tin.
- Server nhận và hiển thị nội dung, đồng thời có thể phản hồi lại cho các client khác.
- Điều này giúp hệ thống trở thành một giải pháp lý tưởng để truyền thông điệp nhanh trong mạng nội bộ.

🌟 2. Ưu điểm của hệ thống

- Đơn giản, dễ triển khai: không yêu cầu thiết lập kết nối phức tạp.
- Tốc độ truyền nhanh: do UDP không có cơ chế xác nhận gói tin nên độ trễ thấp, phù hợp với các ứng dụng thông báo tức thời.
- Minh họa kiến thức lập trình mạng: giúp người học hiểu rõ hơn về socket, broadcast, và cơ chế hoạt động của UDP.
- Ứng dụng thực tiễn: có thể mở rộng để xây dựng các hệ thống chat nội bộ, công cụ gửi thông báo trong LAN, hoặc phần mềm hỗ trợ quản trị hệ thống.

🔧 3. Ngôn ngữ và công nghệ sử dụng

- Thành phần	Mô tả
- Ngôn ngữ lập trình	Java (phiên bản JDK 21 hoặc cao hơn)
- Giao diện người dùng	Java Swing (cửa sổ chat, input, nút gửi/nhận)
- Giao thức mạng	UDP – sử dụng DatagramSocket & DatagramPacket
- IDE khuyến nghị	Eclipse (bản mới nhất)
- Hệ điều hành	Windows, Linux hoặc macOS (chạy được trên tất cả nền tảng hỗ trợ Java)

🚀 4. Hướng dẫn cài đặt và chạy
🔹 Yêu cầu hệ thống

- Eclipse IDE (khuyến nghị bản mới nhất).
- JDK 21+ đã được cài đặt và thiết lập biến môi trường JAVA_HOME.
- Git (dùng để clone project từ GitHub).

🔹 Các bước thực hiện
Bước 1: Lấy project từ GitHub

- Mở terminal hoặc Git Bash, chạy:
git clone <link-repository>

Bước 2: Import vào Eclipse

- Mở Eclipse.
- Chọn File → Import.
- Chọn Existing Projects into Workspace.
- Duyệt đến thư mục project vừa clone về.
- Nhấn Finish để import.

Bước 3: Kiểm tra môi trường

- Đảm bảo project đang dùng JavaSE-21.
- Nếu thiếu thư viện, mở Project → Properties → Java Build Path và thêm JDK tương ứng.

Bước 4: Chạy ứng dụng

- Chạy UDPServerChat trước để khởi động server.
- Sau đó chạy UDPClientChat trên một hoặc nhiều máy để kết nối.

Bước 5: Gửi và nhận tin nhắn

- Nhập tin nhắn vào ô input, nhấn Gửi.
- Các client khác sẽ ngay lập tức nhận được tin nhắn broadcast.
- Có thể nhấn Stop Server / Stop Client để kết thúc phiên chat.

🎨 5. Minh họa giao diện (tùy chọn)


📱 6. Thông tin liên hệ

- Họ và tên: Ngô Thành Đạt
- Lớp: CNTT 16-01
- Email: thanhdatdzhp11@gmail.com
- SĐT: 0901 562 638

© 2025 AIoTLab – Faculty of Information Technology – DaiNam University.
