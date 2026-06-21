# Hướng Dẫn Triển Khai Chi Tiết (Deployment Guide)

Tài liệu này hướng dẫn bạn cách thiết lập và triển khai dự án này lên môi trường production:
1. **Database:** Host MySQL trên **Aiven.io** (Miễn phí).
2. **Backend (Spring Boot):** Triển khai Docker Container lên **Render.com** (Miễn phí).
3. **Keep-Alive Backend:** Thiết lập **Uptime Robot** định kỳ gửi request vào API `/ping` để tránh Render đi ngủ (sleep).
4. **Frontend (Next.js):** Triển khai tĩnh lên **GitHub Pages**.

---

## 1. Thiết Lập Database Trên Aiven.io

Aiven cung cấp dịch vụ cloud database MySQL miễn phí cực kỳ chất lượng.

### Các bước thực hiện:
1. Truy cập [Aiven.io](https://aiven.io/) và đăng ký/đăng nhập tài khoản.
2. Tạo một **Project** mới (hoặc sử dụng project mặc định).
3. Click vào **Create service**:
   - Chọn **MySQL**.
   - Chọn nhà cung cấp cloud (ví dụ: *AWS* hoặc *Google Cloud*).
   - Chọn Region gần bạn nhất (ví dụ: *Singapore* hoặc *Hong Kong* để tối ưu tốc độ).
   - Chọn gói **Free** (Miễn phí).
   - Click **Create service**.
4. Chờ dịch vụ khởi tạo xong (trạng thái chuyển sang `Running`).
5. Trong trang quản lý Service, kéo xuống phần **Connection information**:
   - Chọn tab **Connection details** để lấy các thông tin:
     - **Host:** Tên host (ví dụ: `mysql-xxxx.aivencloud.com`)
     - **Port:** Cổng kết nối (ví dụ: `12345` hoặc `3306`)
     - **User:** Mặc định là `avnadmin`
     - **Password:** Mật khẩu ngẫu nhiên được cấp
     - **Database name:** Tên database mặc định (thường là `defaultdb`)

---

## 2. Triển Khai Backend Lên Render.com

Chúng ta đã tạo sẵn file [Dockerfile](file:///c:/Projects/mid-project-007054055/source/server/Dockerfile) hỗ trợ build tự động bằng Docker, giúp tránh các lỗi không tương thích phiên bản Java trên Render.

### Các bước thực hiện:
1. Truy cập [Render.com](https://render.com/) và đăng nhập.
2. Click **New +** và chọn **Web Service**.
3. Kết nối với tài khoản GitHub của bạn và chọn repository `network-lab-practice-system`.
4. Cấu hình dịch vụ:
   - **Name:** Đặt tên cho dịch vụ (ví dụ: `network-lab-backend`).
   - **Root Directory:** Nhập `source/server` (Rất quan trọng, để Render chỉ build phần backend).
   - **Language:** Chọn **Docker** (Render sẽ tự động dùng file `Dockerfile` đã chuẩn bị).
   - **Branch:** Chọn `main`.
   - **Instance Type:** Chọn **Free**.
5. Kéo xuống phần **Environment Variables** (Biến môi trường) và cấu hình các biến sau:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<HOST>:<PORT>/<DATABASE_NAME>?useSSL=true&requireSSL=false`
     *(Thay thế `<HOST>`, `<PORT>`, `<DATABASE_NAME>` bằng thông tin từ Aiven)*
   - `SPRING_DATASOURCE_USERNAME`: `avnadmin` *(hoặc username bạn tạo trên Aiven)*
   - `SPRING_DATASOURCE_PASSWORD`: `<MẬT_KHẨU_TỪ_AIVEN>`
6. Click **Create Web Service** và chờ Render tự động build & deploy.
7. Sau khi deploy thành công, bạn sẽ nhận được một URL công khai (ví dụ: `https://network-lab-backend.onrender.com`).
8. Hãy kiểm tra thử API ping mới tạo bằng cách truy cập: `https://network-lab-backend.onrender.com/ping`. Nếu nhận được JSON dạng `{"message": "pong"}`, backend đã hoạt động thành công!

---

## 3. Thiết Lập Uptime Robot Để Backend Không Bị Ngủ

Dịch vụ miễn phí của Render sẽ tự động đi ngủ (sleep) sau 15 phút không nhận được yêu cầu nào. Chúng ta sẽ dùng Uptime Robot để gửi tín hiệu "ping" định kỳ, giúp backend luôn hoạt động.

### Các bước thực hiện:
1. Truy cập [Uptime Robot](https://uptimerobot.com/) và tạo tài khoản miễn phí.
2. Tại màn hình Dashboard, click **Add New Monitor**.
3. Cấu hình monitor:
   - **Monitor Type:** Chọn `HTTP(s)`.
   - **Friendly Name:** Đặt tên gợi nhớ (ví dụ: `Ping Render Backend`).
   - **URL (or IP):** Điền URL API ping của bạn: `https://<tên-ứng-dụng-của-bạn>.onrender.com/ping`.
   - **Monitoring Interval:** Chọn `Every 5 minutes` (mỗi 5 phút gọi 1 lần).
4. Click **Create Monitor** (và xác nhận).
5. Từ giờ, Uptime Robot sẽ tự động gọi API `/ping` 5 phút một lần, đảm bảo server Render không bao giờ chuyển sang chế độ ngủ.

---

## 4. Triển Khai Frontend Lên GitHub Pages

Chúng ta sẽ sử dụng GitHub Actions để tự động build Next.js thành dạng tĩnh và đẩy lên GitHub Pages.

### Các bước thực hiện:

#### Bước 1: Cấu hình biến môi trường trên GitHub
Do frontend cần gọi API tới backend trên Render, ta cần chỉ định URL backend thông qua Repository Secret:
1. Vào repository của bạn trên GitHub.
2. Chọn tab **Settings** -> **Secrets and variables** -> **Actions**.
3. Click **New repository secret**.
4. Đặt tên secret là `NEXT_PUBLIC_API_URL`.
5. Giá trị là URL backend của bạn (ví dụ: `https://network-lab-backend.onrender.com`).

#### Bước 2: Cấp quyền chạy Workflow ghi đè gh-pages
1. Cũng trong tab **Settings** -> **Actions** -> **General** của repo.
2. Cuộn xuống phần **Workflow permissions**.
3. Chọn mục **Read and write permissions**.
4. Click **Save**.

#### Bước 3: Đẩy code và chạy tự động
1. Khi bạn push code lên nhánh `main` (bao gồm file cấu hình `.github/workflows/deploy-frontend.yml` vừa tạo), GitHub Actions sẽ tự động kích hoạt.
2. Bạn có thể kiểm tra tiến trình tại tab **Actions** trên GitHub.
3. Khi workflow chạy thành công, nó sẽ tạo ra một nhánh mới tên là `gh-pages` chứa toàn bộ code tĩnh.

#### Bước 4: Kích hoạt GitHub Pages
1. Vào tab **Settings** -> **Pages** trên GitHub repo.
2. Tại mục **Build and deployment** -> **Source**, chọn **Deploy from a branch**.
3. Tại mục **Branch**, chọn nhánh `gh-pages` và thư mục `/ (root)`.
4. Click **Save**.
5. Sau vài phút, trang web của bạn sẽ được hiển thị công khai tại URL: `https://<tên-username-github>.github.io/network-lab-practice-system/`.

> [!NOTE]
> Do Next.js chạy ở chế độ tĩnh (`output: 'export'`), mọi thay đổi route phía client sẽ hoạt động mượt mà. Tuy nhiên, nếu bạn F5 (refresh) trang web trực tiếp tại các đường dẫn phụ (ví dụ: `/problems`), GitHub Pages có thể báo lỗi 404 vì không tìm thấy file HTML tương ứng.
> Để khắc phục hoàn toàn, bạn có thể triển khai frontend lên **Vercel** hoặc **Netlify** (cực kỳ đơn giản chỉ với 3 click, hỗ trợ Next.js native rất tốt và hoàn toàn miễn phí), hoặc sử dụng thủ thuật file `404.html` điều hướng trên GitHub Pages.
