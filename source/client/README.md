# MODULE CLIENT

> 📘 *Sinh viên mô tả phần **client** tại đây. Điền đầy đủ theo framework và bài toán của nhóm.*

---

## 🎯 MỤC TIÊU

Client chịu trách nhiệm:
- Gửi yêu cầu đến server
- Hiển thị kết quả cho người dùng
- Cung cấp giao diện tương tác

---

## ⚙️ CÔNG NGHỆ SỬ DỤNG

| Thành phần | Công nghệ |
|------------|-----------|
| Ngôn ngữ | Node.js / Javascript / Typescript |
| Thư viện chính | Next.js / React / Ant Design / Redux Toolkit / Socket.IO Client |
| Giao thức | HTTP / WebSocket (qua socket.io) |

---

## 🚀 HƯỚNG DẪN CHẠY

### Cài đặt
```bash
npm install
```

### Chạy chương trình
```bash
npm run dev
```

### Cấu hình (nếu cần)
- Server URL: `http://localhost:8888`
- Server Socket URL: `http://localhost:8889`
- Có thể thay đổi trong file `.env.development`

---

## 📦 CẤU TRÚC
```
client/
├── README.md
├── main.js (hoặc client.py)
├── config.js
└── lib/
    └── helper.js
```

---

## 💡 SỬ DỤNG
```bash
# Ví dụ gửi request
node main.js --input "data"
```

---

## 📝 GHI CHÚ

- Đảm bảo server đã chạy trước khi khởi động client
- Mặc định kết nối đến `localhost:8080`