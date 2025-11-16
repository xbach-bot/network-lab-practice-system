# MODULE SERVER

> 📘 _Sinh viên mô tả phần **server** tại đây. Điền đầy đủ theo framework và bài toán của nhóm._

---

## 🎯 MỤC TIÊU

Server chịu trách nhiệm:

- Tiếp nhận yêu cầu từ client thông qua các giao thức mạng (TCP/UDP/RMI/HTTP/WebSocket).
- Xử lý nghiệp vụ theo từng dạng bài (mã sinh viên, mã bài, dữ liệu).
- Thực thi thuật toán/tính toán, xác thực dữ liệu và kiểm tra tính hợp lệ.
- Trả kết quả cho client

---

## ⚙️ CÔNG NGHỆ SỬ DỤNG

| Thành phần | Công nghệ                                              |
| ---------- | ------------------------------------------------------ |
| Ngôn ngữ   | Java                                                   |
| Framework  | Spring Boot(Web, Security, WebSocket, JPA, Validation) |
| Database   | MySQL                                                  |

---

## 🚀 HƯỚNG DẪN CHẠY

### Cài đặt

```bash
mvn clean install
```

### Khởi động server

```bash
mvn spring-boot:run
```

Server chạy tại: `http://localhost:8888`

---

## 🔗 API

| Endpoint                                | Method | Input   | Output                                                                                |
| --------------------------------------- | ------ | ------- | ------------------------------------------------------------------------------------- |
| `/health`                               | GET    | —       | `{ "message": "success", "status": 200, "data": { "message": "OK" }, "error": null }` |
| `/auth/login`                           | POST   | `{ "email": "bachpd@gmail.com", "password": "123456" }` | `{ "message": "success", "status": 200, "data": { "user": { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" }, "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWNocGRAZ21haWwuY29tIiwiZXhwIjoxNzcxOTM4NTM1LCJpYXQiOjE3NjMyOTg1MzUsInVzZXIiOiJ7XCJuYW1lXCI6XCJQaMO5bmcgxJDhu6ljIELDoWNoXCIsXCJlbWFpbFwiOlwiYmFjaHBkQGdtYWlsLmNvbVwifSJ9.USHAWBV2ptaA-ZZv9fyJgmgF9BuXlVyQb2UHWhwHaEw" }, "error": null }`                                                                               |
| `/auth/register`                        | POST   | `{ "email": "test@gmail.com", "name": "test", "password": "123456", "studentId": "abc112233" }` | `{ "message": "success", "status": 200, "data": { "name": "test", "email": "test@gmail.com", "password": "123456", "studentId": "abc112233" }, "error": null }`                                                                               |
| `/auth/account`                         | GET    | —       | `{ "message": "success", "status": 200, "data": { "user": { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" } }, "error": null }`                                                                               |
| `/auth/refresh`                         | GET    | —       | `{ "message": "success", "status": 200, "data": { "user": { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" }, "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWNocGRAZ21haWwuY29tIiwiZXhwIjoxNzcxOTM5MTU4LCJpYXQiOjE3NjMyOTkxNTgsInVzZXIiOiJ7XCJuYW1lXCI6XCJQaMO5bmcgxJDhu6ljIELDoWNoXCIsXCJlbWFpbFwiOlwiYmFjaHBkQGdtYWlsLmNvbVwifSJ9.um30JB87wNRbDOkpz6ApgQLIge0xgYKrkhQJt9m56U4" }, "error": null }`                                                                               |
| `/auth/logout`                          | POST   | —       | `{ "message": "success", "status": 200, "data": null, "error": null }`                                                                               |
| `/chats`                                | POST   | `{ "content": "string", "roomId": null }` | `{ "message": "success", "status": 201, "data": { "id": null, "content": "string", "createdAt": "2025-11-16T13:22:59.055927900Z", "updatedAt": "2025-11-16T13:22:59.055927900Z", "user": { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" } }, "error": null }`                                                                               |
| `/chats`                                | GET    | `{"page": 1, "size" : 2}`     | `{ "message": "success", "status": 200, "data": { "meta": { "current": 1, "pageSize": 2, "pages": 31, "total": 61 }, "result": [ { "id": 1, "content": "db", "createdAt": "2025-11-10T15:25:22.035179Z", "updatedAt": "2025-11-10T15:25:22.035179Z", "user": { "id": 6, "name": "Đào Đức Duy", "email": "duydd@gmail.com", "studentId": "B22DCCN0145", "role": "STUDENT" } }, { "id": 2, "content": "b", "createdAt": "2025-11-10T15:25:28.242107Z", "updatedAt": "2025-11-10T15:25:28.242107Z", "user": { "id": 6, "name": "Đào Đức Duy", "email": "duydd@gmail.com", "studentId": "B22DCCN0145", "role": "STUDENT" } } ] }, "error": null }`                                                                               |
| `/chats/rooms`                          | GET    | `{"page": 1, "size": 2, "roomId": 13}` | `{ "message": "success", "status": 200, "data": { "meta": { "current": 1, "pageSize": 2, "pages": 6, "total": 12 }, "result": [ { "id": 7, "content": "hello", "createdAt": "2025-11-11T15:45:06.391707Z", "updatedAt": "2025-11-11T15:45:06.391707Z", "user": { "id": 6, "name": "Đào Đức Duy", "email": "duydd@gmail.com", "studentId": "B22DCCN0145", "role": "STUDENT" } }, { "id": 145, "content": ":))))))))", "createdAt": "2025-11-11T16:59:35.409573Z", "updatedAt": "2025-11-11T16:59:35.409573Z", "user": { "id": 6, "name": "Đào Đức Duy", "email": "duydd@gmail.com", "studentId": "B22DCCN0145", "role": "STUDENT" } } ] }, "error": null }`                                                                               |
| `/chats/{id}`                           | DELETE | `{"id": 269}` | `{ "message": "success", "status": 200, "data": { "id": 269, "content": "string", "createdAt": "2025-11-16T13:22:59.057931Z", "updatedAt": "2025-11-16T13:22:59.057931Z", "user": { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" } }, "error": null }`                                                                               |
| `/problems`                             | GET    | `{"page": 1, "size": 2}` | `{ "message": "success", "status": 200, "data": { "meta": { "current": 1, "pageSize": 2, "pages": 5, "total": 10 }, "result": [ { "id": 1, "title": "Loại bỏ ký tự đặc biệt, trùng và giữ nguyên thứ tự xuất hiện", "description": "Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2206\n(hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s).\n\nYêu cầu là xây dựng một chương trình client tương tác tới server sử dụng các luồng ký tự (BufferedReader/BufferedWriter) theo kịch bản dưới đây:\n\na. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng \"studentCode;qCode\". Ví dụ: \"B15DCCN999;7D6265E3\"\n\nb. Nhận một chuỗi ngẫu nhiên từ server\n\nc. Loại bỏ ký tự đặc biệt, số, ký tự trùng và giữ nguyên thứ tự xuất hiện của ký tự. Gửi chuỗi đã được xử lý lên server.\n\nd. Đóng kết nối và kết thúc chương trình", "protocolType": "tcp", "type": "tcp-char", "ioType": "BUFFER", "solved": true, "qcode": "xVX7k3lq" }, { "id": 2, "title": "Giải mã Caesar (dịch chuyển ký tự)", "description": "Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s. \nVí dụ: với s = 3 thì ký tự “A” sẽ được thay thế bằng ký tự “D”.\nMột chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). \nYêu cầu là xây dựng chương trình client tương tác với server trên, sử dụng các luồng byte (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:\na. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng \"studentCode;qCode\". Ví dụ: \"B15DCCN999;D68C93F7\"\nb. Nhận lần lượt chuỗi đã bị mã hóa caesar và giá trị dịch chuyển s nguyên\nc. Thực hiện giải mã ra thông điệp ban đầu và gửi lên Server\nd. Đóng kết nối và kết thúc chương trình.", "protocolType": "tcp", "type": "tcp-byte", "ioType": "DATA", "solved": true, "qcode": "x77snUdo" } ] }, "error": null }`                                                                               |
| `/problems/get-one/{qCode}`             | GET    | `{"qCode": "x77snUdo"}` | `{ "message": "success", "status": 200, "data": { "id": 2, "title": "Giải mã Caesar (dịch chuyển ký tự)", "description": "Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s. \nVí dụ: với s = 3 thì ký tự “A” sẽ được thay thế bằng ký tự “D”.\nMột chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). \nYêu cầu là xây dựng chương trình client tương tác với server trên, sử dụng các luồng byte (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:\na. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng \"studentCode;qCode\". Ví dụ: \"B15DCCN999;D68C93F7\"\nb. Nhận lần lượt chuỗi đã bị mã hóa caesar và giá trị dịch chuyển s nguyên\nc. Thực hiện giải mã ra thông điệp ban đầu và gửi lên Server\nd. Đóng kết nối và kết thúc chương trình.", "protocolType": "tcp", "type": "tcp-byte", "ioType": "DATA", "solved": true, "qcode": "x77snUdo" }, "error": null }`                                                                               |
| `/rooms/me`                             | GET    | —       | `{ "message": "success", "status": 200, "data": [ { "id": 13, "name": "private_3_6", "participants": [ { "id": 6, "name": "Đào Đức Duy", "email": "duydd@gmail.com", "studentId": "B22DCCN0145", "role": "STUDENT" }, { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" } ] }, { "id": 16, "name": "private_2_3", "participants": [ { "id": 2, "name": "Trần Quốc An", "email": "antq@gmail.com", "studentId": "B22DCCN007", "role": "STUDENT" }, { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" } ] }, { "id": 18, "name": "private_3_5", "participants": [ { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" }, { "id": 5, "name": "ab", "email": "antq23@gmail.com", "studentId": "12345666", "role": "STUDENT" } ] } ], "error": null }`                                                                               |
| `/rooms/private`                        | POST   | `{"targetUserId": 2}` | `{ "message": "success", "status": 200, "data": { "id": 16, "name": "private_2_3", "createdAt": "2025-11-11T16:34:42.944873Z" }, "error": null }`                                                                               |
| `/submissions`                          | GET    | `{"page": 1, "size": 1}` | `{ "message": "success", "status": 200, "data": { "meta": { "current": 1, "pageSize": 1, "pages": 18, "total": 18 }, "result": [ { "id": 54, "inputData": "yBvzrAtllibg", "studentResult":"addaadaa`                                                                               |
| `/submissions/by-qcode/{qCode}`         | GET    | `{"qCode": "x77snUdo", "page": 1, "size": 1}` | `{ "message": "success", "status": 200, "data": { "meta": { "current": 1, "pageSize": 1, "pages": 4, "total": 4 }, "result": [ { "id": 47, "inputData": "IHNVYPBJPTSRPQLCY;2", "studentResult": "TimeLimitExceeded", "expectedResult": "GFLTWNZHNRQPNOJAW", "correct": false, "createdAt": "2025-11-15T04:46:40.013251Z", "status": "Sai", "user": { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" }, "problem": { "id": 2, "title": "Giải mã Caesar (dịch chuyển ký tự)", "description": "Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s. \nVí dụ: với s = 3 thì ký tự “A” sẽ được thay thế bằng ký tự “D”.\nMột chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). \nYêu cầu là xây dựng chương trình client tương tác với server trên, sử dụng các luồng byte (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:\na. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng \"studentCode;qCode\". Ví dụ: \"B15DCCN999;D68C93F7\"\nb. Nhận lần lượt chuỗi đã bị mã hóa caesar và giá trị dịch chuyển s nguyên\nc. Thực hiện giải mã ra thông điệp ban đầu và gửi lên Server\nd. Đóng kết nối và kết thúc chương trình.", "protocolType": "tcp", "type": "tcp-byte", "ioType": "DATA", "solved": false, "qcode": "x77snUdo" } } ] }, "error": null }`                                                                               |
| `/submissions/user/ranking`             | GET    | `{"page": 1, "size": 3}` | `{ "message": "success", "status": 200, "data": { "meta": { "current": 1, "pageSize": 3, "pages": 2, "total": 5 }, "result": [ { "user": { "id": 3, "name": "Phùng Đức Bách", "email": "bachpd@gmail.com", "studentId": "B22DCCN055", "role": "STUDENT" }, "totalSubmissions": 18, "correctSubmissions": 4 }, { "user": { "id": 2, "name": "Trần Quốc An", "email": "antq@gmail.com", "studentId": "B22DCCN007", "role": "STUDENT" }, "totalSubmissions": 30, "correctSubmissions": 4 }, { "user": { "id": 5, "name": "ab", "email": "antq23@gmail.com", "studentId": "12345666", "role": "STUDENT" }, "totalSubmissions": 0, "correctSubmissions": 0 } ] }, "error": null }`                                                                               |
| `/submit-file/problems/{qcode}/upload`  | POST   | `{"file": "Test16.java", "qCode": "x77snUdo"}` | `{ "message": "success", "status": 200, "data": { "id": 15, "filePath": "source\\server\\public\\submissions\\2\\1763301018182_Test16.java", "createdAt": "2025-11-16T13:50:18.203860500Z", "problem": { "id": 2, "title": "Giải mã Caesar (dịch chuyển ký tự)", "description": "Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s. \nVí dụ: với s = 3 thì ký tự “A” sẽ được thay thế bằng ký tự “D”.\nMột chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). \nYêu cầu là xây dựng chương trình client tương tác với server trên, sử dụng các luồng byte (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:\na. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng \"studentCode;qCode\". Ví dụ: \"B15DCCN999;D68C93F7\"\nb. Nhận lần lượt chuỗi đã bị mã hóa caesar và giá trị dịch chuyển s nguyên\nc. Thực hiện giải mã ra thông điệp ban đầu và gửi lên Server\nd. Đóng kết nối và kết thúc chương trình.", "protocolType": "tcp", "type": "tcp-byte", "ioType": "DATA", "solved": false, "qcode": "x77snUdo" } }, "error": null }`                                                                               |
| `/submit-file/me`                       | GET    | `{"page": 1, "size": 1}` | `{ "message": "success", "status": 200, "data": { "meta": { "current": 1, "pageSize": 1, "pages": 6, "total": 6 }, "result": [ { "id": 15, "filePath": "source\\server\\public\\submissions\\2\\1763301018182_Test16.java", "createdAt": "2025-11-16T13:50:18.191103Z", "problem": { "id": 2, "title": "Giải mã Caesar (dịch chuyển ký tự)", "description": "Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s. \nVí dụ: với s = 3 thì ký tự “A” sẽ được thay thế bằng ký tự “D”.\nMột chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). \nYêu cầu là xây dựng chương trình client tương tác với server trên, sử dụng các luồng byte (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:\na. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng \"studentCode;qCode\". Ví dụ: \"B15DCCN999;D68C93F7\"\nb. Nhận lần lượt chuỗi đã bị mã hóa caesar và giá trị dịch chuyển s nguyên\nc. Thực hiện giải mã ra thông điệp ban đầu và gửi lên Server\nd. Đóng kết nối và kết thúc chương trình.", "protocolType": "tcp", "type": "tcp-byte", "ioType": "DATA", "solved": true, "qcode": "x77snUdo" } } ] }, "error": null }`                                                                               |
| `/submit-file/submissions/{id}/content` | GET    | `{"id": 15}` | `{ "message": "success", "status": 200, "data": { "message": "import java.io.*;\r\nimport java.util.*;\r\n\r\npublic class Test16 {\r\n    public static void main(String[] args) throws IOException {\r\n        Scanner sc = new Scanner(System.in);\r\n        int n = sc.nextInt();\r\n        int[] A = new int[n];\r\n        for (int i = 0; i < n; i++) {\r\n            A[i] = sc.nextInt();\r\n        }\r\n\r\n        long count = 0;\r\n        Stack<Integer> st = new Stack<>();\r\n\r\n        for (int i = 0; i < n; i++) {\r\n            while (!st.isEmpty() && A[st.peek()] < A[i]) {\r\n                int j = st.pop();\r\n                count += (i - j);\r\n            }\r\n            st.push(i);\r\n        }\r\n\r\n        while (!st.isEmpty()) {\r\n            int j = st.pop();\r\n            count += (n - j - 1);\r\n        }\r\n\r\n        System.out.println(count);\r\n    }\r\n}" }, "error": null }`                                                                               |

> **Lưu ý:** Bổ sung các endpoint của nhóm vào bảng trên.

---

## 📦 CẤU TRÚC

```
server/
├── .mvn
│   └── wrapper
│       └── maven-wrapper.properties
├── public
│   └── submissions
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── test
│   │   │               ├── config
│   │   │               │   ├── CustomAuthenticationEntryPoint.java
│   │   │               │   ├── CustomCorsConfiguration.java
│   │   │               │   ├── GsonConfig.java
│   │   │               │   ├── HandlerInfo.java
│   │   │               │   ├── HandlerRegistry.java
│   │   │               │   ├── JpaConverterJson.java
│   │   │               │   ├── ModelMapperConfig.java
│   │   │               │   ├── NimbusConfig.java
│   │   │               │   ├── RestTemplateConfig.java
│   │   │               │   ├── SecurityConfiguration.java
│   │   │               │   ├── ServerCommandLineRunnerConfig.java
│   │   │               │   ├── SocketIOConfig.java
│   │   │               │   └── UserDetailCustom.java
│   │   │               ├── controller
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── ChatController.java
│   │   │               │   ├── HealthController.java
│   │   │               │   ├── ProblemController.java
│   │   │               │   ├── RoomController.java
│   │   │               │   ├── SubmissionController.java
│   │   │               │   └── SubmissionFileController.java
│   │   │               ├── core
│   │   │               │   ├── error
│   │   │               │   │   ├── BadRequestException.java
│   │   │               │   │   ├── ForbiddenException.java
│   │   │               │   │   └── UnauthorizedException.java
│   │   │               │   ├── GlobalException.java
│   │   │               │   └── Response.java
│   │   │               ├── domain
│   │   │               │   ├── request
│   │   │               │   │   ├── chat
│   │   │               │   │   │   └── CreateChatDTO.java
│   │   │               │   │   ├── room
│   │   │               │   │   │   ├── AddParticipantDTO.java
│   │   │               │   │   │   ├── CreateRoomDTO.java
│   │   │               │   │   │   └── PrivateRoomDTO.java
│   │   │               │   │   ├── user
│   │   │               │   │   │   └── RegisterUserDTO.java
│   │   │               │   │   └── RequestLoginDTO.java
│   │   │               │   ├── response
│   │   │               │   │   ├── chat
│   │   │               │   │   │   └── ResponseChatDTO.java
│   │   │               │   │   ├── problem
│   │   │               │   │   │   ├── ProblemResult.java
│   │   │               │   │   │   └── ResponseProblemDTO.java
│   │   │               │   │   ├── room
│   │   │               │   │   │   └── ResponseRoomDTO.java
│   │   │               │   │   ├── submission
│   │   │               │   │   │   └── ResponseSubmissionDTO.java
│   │   │               │   │   ├── submissionfile
│   │   │               │   │   │   └── ResponseSubmissionFileDTO.java
│   │   │               │   │   ├── user
│   │   │               │   │   │   ├── IUserRank.java
│   │   │               │   │   │   ├── ResponseUserDTO.java
│   │   │               │   │   │   └── ResponseUserRankDTO.java
│   │   │               │   │   ├── ResponseLoginDTO.java
│   │   │               │   │   ├── ResponseMetaDTO.java
│   │   │               │   │   ├── ResponsePaginationDTO.java
│   │   │               │   │   └── ResponseString.java
│   │   │               │   ├── Chat.java
│   │   │               │   ├── Problem.java
│   │   │               │   ├── Room.java
│   │   │               │   ├── Submission.java
│   │   │               │   ├── SubmissionFile.java
│   │   │               │   └── User.java
│   │   │               ├── handler
│   │   │               │   ├── tcp
│   │   │               │   │   ├── BinaryConvertHandler.java
│   │   │               │   │   ├── CaesarDecryptHandler.java
│   │   │               │   │   ├── CollatzRawHandler.java
│   │   │               │   │   ├── DuplicateCharCountHandler.java
│   │   │               │   │   └── SpecialCharFilterHandler.java
│   │   │               │   ├── udp
│   │   │               │   │   ├── MinMaxUdpHandler.java
│   │   │               │   │   ├── SlidingWindowMaxHandler.java
│   │   │               │   │   └── UdpFilterCharsHandler.java
│   │   │               │   └── ProblemHandler.java
│   │   │               ├── repository
│   │   │               │   ├── ChatRepository.java
│   │   │               │   ├── ProblemRepository.java
│   │   │               │   ├── RoomRepository.java
│   │   │               │   ├── SubmissionFileRepository.java
│   │   │               │   ├── SubmissionRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               ├── rmi
│   │   │               │   ├── byteservice
│   │   │               │   │   └── ByteServiceImpl.java
│   │   │               │   ├── characterservice
│   │   │               │   │   └── CharacterServiceImpl.java
│   │   │               │   ├── ByteService.java
│   │   │               │   ├── CharacterService.java
│   │   │               │   └── RmiServer.java
│   │   │               ├── service
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── ChatService.java
│   │   │               │   ├── JwtService.java
│   │   │               │   ├── ProblemService.java
│   │   │               │   ├── RoomService.java
│   │   │               │   ├── SocketService.java
│   │   │               │   ├── SubmissionFileService.java
│   │   │               │   ├── SubmissionService.java
│   │   │               │   └── UserService.java
│   │   │               ├── socket
│   │   │               │   ├── tcp
│   │   │               │   │   ├── TcpBufferedServer.java
│   │   │               │   │   ├── TcpServerData.java
│   │   │               │   │   └── TcpServerRaw.java
│   │   │               │   ├── udp
│   │   │               │   │   └── UdpServer.java
│   │   │               │   └── websocket
│   │   │               │       └── AppGateway.java
│   │   │               ├── utils
│   │   │               │   └── FormatResponse.java
│   │   │               └── TestApplication.java
│   │   └── resources
│   │       ├── static
│   │       │   └── favicon.ico
│   │       └── application.yml
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── test
│                       └── TestApplicationTests.java
├── .gitignore
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

---

## 🧪 TEST

```bash
# Test API bằng curl
curl http://localhost:8888/health
```

---

## 📝 GHI CHÚ

- Port mặc định: **8888**
- Có thể thay đổi trong file `application.yml`
