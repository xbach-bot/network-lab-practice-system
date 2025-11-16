# MODULE CLIENT

> 📘 _Sinh viên mô tả phần **client** tại đây. Điền đầy đủ theo framework và bài toán của nhóm._

---

## 🎯 MỤC TIÊU

Client chịu trách nhiệm:

- Gửi yêu cầu đến server
- Hiển thị kết quả cho người dùng
- Cung cấp giao diện tương tác

---

## ⚙️ CÔNG NGHỆ SỬ DỤNG

| Thành phần     | Công nghệ                                                       |
| -------------- | --------------------------------------------------------------- |
| Ngôn ngữ       | Javascript / Typescript                                         |
| Thư viện chính | Next.js / React / Ant Design / Redux Toolkit / Socket.IO Client |
| Giao thức      | HTTP / WebSocket (qua socket.io)                                |

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
├── src
│   ├── app
│   │   ├── login
│   │   │   └── page.tsx
│   │   ├── problems
│   │   │   └── [qcode]
│   │   │       └── page.tsx
│   │   ├── ranking
│   │   │   └── page.tsx
│   │   ├── register
│   │   │   └── page.tsx
│   │   ├── submission-file
│   │   │   └── page.tsx
│   │   ├── submissions
│   │   │   └── page.tsx
│   │   ├── StoreProvider.tsx
│   │   ├── layout.tsx
│   │   └── page.tsx
│   ├── components
│   │   ├── client
│   │   │   ├── Chat
│   │   │   │   ├── Chat.modal.tsx
│   │   │   │   ├── Chat.private.tsx
│   │   │   │   ├── Message.card.tsx
│   │   │   │   └── RoomsDrawer.tsx
│   │   │   └── Header
│   │   │       └── Header.tsx
│   │   └── layout
│   │       └── LayoutApp.tsx
│   ├── config
│   │   └── api.ts
│   ├── hooks
│   │   └── debounce.input.tsx
│   ├── lib
│   │   ├── redux
│   │   │   ├── slice
│   │   │   │   ├── auth.slice.ts
│   │   │   │   └── chat.slice.ts
│   │   │   ├── hooks.ts
│   │   │   └── store.ts
│   │   └── antd.registry.tsx
│   ├── styles
│   │   ├── Chat.module.scss
│   │   ├── ClientLayout.scss
│   │   ├── Header.module.scss
│   │   ├── Home.module.scss
│   │   ├── Login.module.scss
│   │   ├── Problem.module.scss
│   │   └── Register.module.scss
│   ├── types
│   │   └── backend.d.ts
│   └── utils
│       └── socket.ts
├── .eslintrc.json
├── .gitignore
├── README.md
├── next.config.mjs
├── package-lock.json
├── package.json
└── tsconfig.json
```

---

## 💡 SỬ DỤNG

```bash
npm install
npm run dev
```

---

## 📝 GHI CHÚ
- Đảm bảo server đã chạy trước khi khởi động client
- Mặc định kết nối đến `localhost:8888`
