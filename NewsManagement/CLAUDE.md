# NewsManagement – Module 1: Editorial Workflow & Approval

File này được nạp vào mỗi phiên làm việc. Đọc kỹ trước khi sửa code. Thiết kế đầy đủ nằm ở `Module_1_Don_Gian_v2.docx` (nguồn sự thật; nếu có mâu thuẫn thì theo docx và báo lại cho người dùng).

## Mục tiêu

Nâng cấp dự án CRUD News/Categories/Users đơn giản thành **quy trình biên tập và phê duyệt tin tức** (Module 1 trong SRS ngày 10/10/2026; **không** làm Module 2 – Comment).

Đây là **bài tập môn học Spring Boot + React JS** do nhóm 3 người làm, không phải đồ án lớn. Luôn chọn cách đơn giản nhất đủ đáp ứng SRS.

## Nguyên tắc bắt buộc

- Giữ mức vừa phải: không thêm bảng, thư viện, cấu hình hay tính năng ngoài danh sách dưới đây. Không làm cấu hình quy tắc biên tập, không bảng Notification, không event/queue/cache.
- Chỉ **4 entity**: `UserAccount`, `Category`, `NewsArticle`, `WorkflowAuditLog`.
- Mọi đổi trạng thái bài đi qua **một** `WorkflowService`, trong `@Transactional`, và ghi **một** audit log. API CRUD cũ không được sửa `status` hoặc `author` tự do.
- Dùng DTO request/response. Không trả `passwordHash`. Tác giả và trạng thái do backend gán.
- Kiểm tra quyền ở service (role + tác giả sở hữu + trạng thái hiện tại), không chỉ ở controller.
- Prefix API: `/api/v1`. Thời gian lưu `Instant`, React hiển thị giờ Việt Nam.
- Không đưa cơ chế bỏ qua auth vào bản nộp. Không xóa DB cũ khi chưa sao lưu (`ddl-auto=update` không tự chuyển dữ liệu role/status cũ sang enum).
- Giữ cấu trúc hiện có: package `com.minhhai.newsmanagement` (controller, service, service/impl, repository, entity, dto, security, config, exception). Không dựng cấu trúc mới.

## Công nghệ và vị trí

- Backend: Spring Boot, Java 17, JPA, SQL Server, Spring Security + JWT (thư mục này).
- Frontend: React JS ở `../DoMinhHai_SE1985-JV`.
- Bỏ qua thư mục `src/main copy` (bản sao thừa, không dùng).
- Có hai repository người dùng (`UserAccountRepository`, `UserRepository`): kiểm tra và thống nhất về một cái khi gặp.

## Actors

| Actor | Việc chính |
|---|---|
| Contributor | Viết/sửa bài của mình, gửi duyệt, xem feedback, sửa và gửi lại |
| Editor | Xem queue, yêu cầu sửa (feedback bắt buộc), duyệt đăng ngay hoặc hẹn giờ, gỡ bài |
| Admin | Quản lý Users/Categories, xem lịch sử, override có lý do |
| Reader | Xem bài PUBLISHED, không cần đăng nhập, không có role trong DB |
| Scheduler | Tiến trình hệ thống tự đăng bài đến giờ, actor log để trống |

`UserRole { ADMIN, EDITOR, CONTRIBUTOR }`
`ArticleStatus { DRAFT, PENDING_REVIEW, CHANGES_REQUESTED, APPROVED_SCHEDULED, PUBLISHED, ARCHIVED }`
`WorkflowAction { CREATE_DRAFT, SUBMIT, RESUBMIT, REQUEST_CHANGES, APPROVE_NOW, SCHEDULE, AUTO_PUBLISH, ARCHIVE, OVERRIDE }`

## Bảng chuyển trạng thái (chỉ cho phép đúng các dòng này, sai trả 409)

| Từ | Action | Đến | Ai | Điều kiện |
|---|---|---|---|---|
| (chưa có) | CREATE_DRAFT | DRAFT | Contributor | Có tiêu đề |
| DRAFT | SUBMIT | PENDING_REVIEW | Tác giả | Đủ title, content, category |
| CHANGES_REQUESTED | RESUBMIT | PENDING_REVIEW | Tác giả | Như SUBMIT |
| PENDING_REVIEW | REQUEST_CHANGES | CHANGES_REQUESTED | Editor | Feedback bắt buộc |
| PENDING_REVIEW | APPROVE_NOW | PUBLISHED | Editor | Không có giờ hẹn |
| PENDING_REVIEW | SCHEDULE | APPROVED_SCHEDULED | Editor | Giờ hẹn trong tương lai |
| APPROVED_SCHEDULED | AUTO_PUBLISH | PUBLISHED | Scheduler | Giờ hẹn ≤ hiện tại |
| PUBLISHED / APPROVED_SCHEDULED | ARCHIVE | ARCHIVED | Editor, Admin | |
| Bất kỳ | OVERRIDE | Do Admin chọn | Admin | Lý do bắt buộc, dữ liệu hợp lệ |

Quy tắc đi kèm:
- Tác giả chỉ sửa bài **của mình** khi `DRAFT` hoặc `CHANGES_REQUESTED`; từ `PENDING_REVIEW` trở đi bị khóa (kể cả `APPROVED_SCHEDULED`).
- `ARCHIVED` là trạng thái cuối; muốn đăng lại phải dùng override.
- DRAFT chỉ tác giả xem; Reader/public chỉ thấy `PUBLISHED`.
- `editorFeedback` giữ bản mới nhất, không xóa khi gửi lại; bản cũ nằm trong audit log.
- Lưu nháp: `content` và category được phép trống; kiểm tra đủ khi SUBMIT/RESUBMIT.
- `@Version` trên `NewsArticle`; xung đột trả 409 kèm thông báo tải lại.
- User có lịch sử thì khóa (không xóa); category đang dùng thì vô hiệu hóa (bài đã đăng vẫn hiển thị); bài thì Archive, không có chức năng xóa.
- Mỗi request JWT phải kiểm tra tài khoản còn active.
- Audit log chỉ thêm: không API sửa/xóa, không cascade xóa theo bài.
- Scheduler quét mỗi 10 giây, xử lý cả bài quá hạn khi khởi động lại, mỗi bài một transaction, bỏ qua bài bị xung đột version.

## API thống nhất

| API | Người làm |
|---|---|
| `POST /auth/login`, `GET /auth/me` | A |
| `POST /news`; `PUT /news/{id}`; `GET /news/my`, `/news/{id}` | B |
| `POST /news/{id}/submit`, `/news/{id}/resubmit` | B (controller) gọi WorkflowService của A |
| `GET /news/my/changes-count` (badge thông báo) | B |
| `GET /editorial/queue` (PENDING_REVIEW); `GET /editorial/articles?status=APPROVED_SCHEDULED\|PUBLISHED`; `GET /editorial/{id}` | C |
| `POST /editorial/{id}/request-changes`, `/approve`, `/archive` | A (React do C) |
| `POST /admin/news/{id}/override` | A |
| `GET /news/{id}/audit-logs` (tác giả bài mình; Editor/Admin bài không phải DRAFT) | C |
| `GET /public/news`, `/public/news/{id}` (chỉ PUBLISHED, lọc theo category) | B |
| `/users`, `/categories` (Admin ghi; Contributor đọc category active) | C |

Approve: `scheduledPublishDate` rỗng = đăng ngay, tương lai = hẹn giờ. Sai quyền 403, sai trạng thái 409.

## Phân công (mỗi người làm cả API và React của phần mình)

- **A (40%)**: JWT, Security, đăng nhập, bảo vệ route theo role; `WorkflowService`, Scheduler; API command Editor/Admin; màn hình override.
- **B (35%)**: News CRUD của Contributor, form bài viết, trạng thái/feedback/badge, nút gửi duyệt/gửi lại; public API và trang đọc tin.
- **C (25%)**: Users/Categories (Admin), API và React Editor (queue, danh sách bài, feedback, duyệt/hẹn giờ, archive), audit timeline.
- Chỉ một người sửa mỗi file chung; đổi entity phải báo trước. Logic chuyển trạng thái chỉ nằm ở `WorkflowService`.

## Việc cần làm (đánh dấu khi xong, cập nhật file này sau mỗi phần)

Nền tảng
- [x] Entity: `UserRole`, `ArticleStatus`, `WorkflowAction`, `UserAccount`, `Category`, `NewsArticle` (thêm field mới, `@Version`), `WorkflowAuditLog`
- [ ] Chuyển dữ liệu role/status cũ sang enum (sao lưu DB trước) — bỏ qua nếu dùng DB mới
- [x] DTO chung và đường dẫn API `/api/v1`
- [x] JWT + Security theo role, kiểm tra user active, `GET /auth/me`
- [x] Đăng nhập React, current user, route theo role

Workflow (A)
- [x] `WorkflowService`: submit, resubmit, requestChanges, approve (ngay/hẹn giờ), archive, override
- [x] Kiểm tra bảng chuyển trạng thái, quyền, tác giả; ghi audit trong transaction
- [x] Scheduler 10 giây, xử lý bài quá hạn
- [x] API command Editor/Admin và màn hình override

Contributor và public (B)
- [x] Tạo/sửa Draft, danh sách và chi tiết bài của tôi (log `CREATE_DRAFT`)
- [x] Submit/resubmit controller, `changes-count`
- [x] React: form bài viết, chọn category, trạng thái/feedback, badge, nút gửi duyệt/gửi lại
- [x] Public API và trang đọc tin (chỉ PUBLISHED, lọc category), kiểm tra không lộ bài chưa đăng

Editor và quản trị (C)
- [x] Users/Categories: Admin tạo/sửa/khóa, gán role, danh mục (cả API và React)
- [x] API queue, danh sách bài theo trạng thái, chi tiết, audit logs
- [x] React Editor: queue, xem bài, feedback, duyệt ngay/hẹn giờ, archive (tối đa 3 lần bấm)
- [x] Audit timeline

Tích hợp (A)
- [x] Chạy trọn luồng (đã kiểm thử tự động trên H2 + thử tay React): login → draft → submit → feedback/resubmit → approve/schedule → public → archive → audit

## Checklist trước khi nộp (Definition of Done)

- [x] Contributor gửi bài xong không sửa được (kể cả khi đang hẹn giờ); bài người khác không sửa được
- [x] Editor trả bài có feedback; tác giả thấy badge, sửa và nộp lại
- [x] Đăng ngay và hẹn giờ đều chạy; Reader chỉ thấy PUBLISHED
- [x] Archive ẩn bài công khai (kể cả bài đang hẹn giờ)
- [x] Mọi lần đổi trạng thái có audit, kể cả Scheduler và override
- [x] Chuyển trạng thái sai (vd DRAFT → PUBLISHED qua API thường) bị từ chối
- [x] Users/Categories chỉ Admin quản lý; response không có `passwordHash`; token của tài khoản bị khóa không dùng được
- [ ] Queue và thao tác phản ánh trong 2 giây (đo bằng tab Network); duyệt/yêu cầu sửa tối đa 3 lần bấm từ queue
- [ ] Ghi rõ trong báo cáo: khả dụng 99,5% chỉ áp dụng cho môi trường demo; độ trễ quét Scheduler

## Trạng thái hiện tại

- Backend và React đã hiện thực đủ phạm vi trên. Test tích hợp `WorkflowFlowTest` (H2 in-memory, `mvn test`) chạy trọn luồng và phân quyền.
- Chưa chạy trên SQL Server thật: mật khẩu `sa` trong `application.properties` không đăng nhập được vào container hiện có. DB cũ có cột role/status kiểu số nên cần dùng DB mới (đổi `databaseName`) hoặc xóa bảng cũ.
- Tài khoản mẫu (DataInitializer): admin/admin123, editor/editor123, contributor1/123456, contributor2/123456.

## Cách làm việc với Claude trong repo này

- Trước khi code, đối chiếu yêu cầu với file này; nếu việc yêu cầu vượt phạm vi, nói rõ thay vì tự mở rộng.
- Làm đúng phần được giao, sửa tối thiểu, theo style code hiện có; không refactor ngoài yêu cầu.
- Sau khi xong một mục, tích `[x]` ở danh sách trên và nói rõ phần nào chưa kiểm chứng.
- Chưa chắc về luật nghiệp vụ thì hỏi, không tự đoán.
