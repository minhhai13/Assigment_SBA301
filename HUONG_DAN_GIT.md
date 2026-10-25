# Hướng dẫn Git: chia việc 3 người

Repo: `https://github.com/minhhai13/Assigment_SBA301.git` (thư mục gốc chứa `NewsManagement/` và `DoMinhHai_SE1985-JV/`).

## Cách hoạt động

1. **A** commit phần lõi lên `main`, **không gồm file của B và C**.
2. **A** đẩy thêm nhánh `solution-draft` chỉ chứa file của B và C (để B và C lấy đúng file của mình).
3. **B** và **C** clone, tạo nhánh riêng, lấy **đúng file của mình** từ `solution-draft`, kiểm tra, commit, push, mở Pull Request.
4. **A** merge hai PR rồi chạy `mvn test` và `npm run build` để kiểm tra cả dự án.

Sau bước 1, `main` biên dịch được (backend) nhưng `npm run build` chưa chạy được và `WorkflowFlowTest` chưa qua cho đến khi B và C merge (vì `App.jsx` import trang của B/C và test gọi API của C).

---

## Danh sách file

**B: CRUD Users**
```
NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/UserController.java
NewsManagement/src/main/java/com/minhhai/newsmanagement/service/UserService.java
NewsManagement/src/main/java/com/minhhai/newsmanagement/dto/UserRequest.java
DoMinhHai_SE1985-JV/src/pages/UserManage.jsx
```

**C: CRUD News của Contributor + trang đọc tin công khai**
```
NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/NewsController.java
NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/PublicNewsController.java
NewsManagement/src/main/java/com/minhhai/newsmanagement/service/NewsService.java
NewsManagement/src/main/java/com/minhhai/newsmanagement/dto/ArticleRequest.java
DoMinhHai_SE1985-JV/src/components/ArticleForm.jsx
DoMinhHai_SE1985-JV/src/pages/MyArticles.jsx
DoMinhHai_SE1985-JV/src/pages/PublicNews.jsx
```

**A**: tất cả file còn lại (entity, enums, security, workflow, scheduler, editorial, admin, category, test, khung React...).

---

## Việc của A (chạy ở thư mục gốc `Assignment/`)

Bước 1. Commit phần lõi lên `main` (loại file của B, C và file rác):

```bash
git add -A -- . ':!.idea' ':(exclude,glob)**/.DS_Store'
```

```bash
git rm -rf --cached -q --ignore-unmatch -- \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/UserController.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/service/UserService.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/dto/UserRequest.java \
 DoMinhHai_SE1985-JV/src/pages/UserManage.jsx \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/NewsController.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/PublicNewsController.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/service/NewsService.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/dto/ArticleRequest.java \
 DoMinhHai_SE1985-JV/src/components/ArticleForm.jsx \
 DoMinhHai_SE1985-JV/src/pages/MyArticles.jsx \
 DoMinhHai_SE1985-JV/src/pages/PublicNews.jsx
```

Kiểm tra: không được thấy file `.DS_Store` hay `.idea`; các file cũ của B, C chỉ hiện dạng `D` (xóa khỏi commit, còn nguyên trên đĩa).

```bash
git status --short
```

```bash
git commit -m "Core: entity, enums, security, workflow, scheduler, editorial, admin, category, khung React"
```

Bước 2. Tạo nhánh `solution-draft` chỉ chứa file của B và C rồi đẩy lên:

```bash
git switch -c solution-draft
```

```bash
git add -A -- . ':!.idea' ':(exclude,glob)**/.DS_Store'
```

```bash
git commit -m "Draft: file của B (Users) và C (News) để lấy về commit"
```

```bash
git push -u origin solution-draft
```

Bước 3. Quay lại `main` và đẩy:

```bash
git switch main
```

```bash
git push -u origin main
```

Sau khi B và C đã lấy file xong và PR đã merge, xóa nhánh nháp: `git push origin --delete solution-draft`.

---

## Việc của B (Users)

Lần đầu: đặt tên và email để commit mang đúng tên mình.

```bash
git config user.name "Tên của B"
git config user.email "email-cua-B@example.com"
```

Clone và tạo nhánh:

```bash
git clone https://github.com/minhhai13/Assigment_SBA301.git
cd Assigment_SBA301
git switch -c feature/user-crud
```

Lấy **chỉ 4 file của B** từ nhánh nháp (vào cả working tree và staging):

```bash
git fetch origin
git restore --source=origin/solution-draft --staged --worktree -- \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/UserController.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/service/UserService.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/dto/UserRequest.java \
 DoMinhHai_SE1985-JV/src/pages/UserManage.jsx
```

Kiểm tra: danh sách dưới đây phải có **đúng 4 file**, không thêm gì khác.

```bash
git diff --cached --name-only
```

Commit và push:

```bash
git commit -m "Feature: CRUD Users (API + trang quản lý)"
git push -u origin feature/user-crud
```

Sau đó mở Pull Request `feature/user-crud` → `main` trên GitHub.

Nếu sửa tiếp file của mình, chỉ add đúng 4 file đó:

```bash
git add NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/UserController.java NewsManagement/src/main/java/com/minhhai/newsmanagement/service/UserService.java NewsManagement/src/main/java/com/minhhai/newsmanagement/dto/UserRequest.java DoMinhHai_SE1985-JV/src/pages/UserManage.jsx
```

---

## Việc của C (News)

```bash
git config user.name "Tên của C"
git config user.email "email-cua-C@example.com"
```

```bash
git clone https://github.com/minhhai13/Assigment_SBA301.git
cd Assigment_SBA301
git switch -c feature/news-crud
```

Lấy **chỉ 7 file của C**:

```bash
git fetch origin
git restore --source=origin/solution-draft --staged --worktree -- \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/NewsController.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/controller/PublicNewsController.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/service/NewsService.java \
 NewsManagement/src/main/java/com/minhhai/newsmanagement/dto/ArticleRequest.java \
 DoMinhHai_SE1985-JV/src/components/ArticleForm.jsx \
 DoMinhHai_SE1985-JV/src/pages/MyArticles.jsx \
 DoMinhHai_SE1985-JV/src/pages/PublicNews.jsx
```

Kiểm tra: phải có **đúng 7 file**.

```bash
git diff --cached --name-only
```

```bash
git commit -m "Feature: CRUD News cho Contributor + trang đọc tin công khai"
git push -u origin feature/news-crud
```

Sau đó mở Pull Request `feature/news-crud` → `main`.

---

## Khi cần cập nhật từ `main`

```bash
git fetch origin
git rebase origin/main
```

Nếu `git status` hiện file không thuộc danh sách của mình, **không add** các file đó. Hỏi A.

## Quy tắc

- Chỉ sửa và commit file trong danh sách của mình. File khác (entity, security, `ArticleResponse`, `UserResponse`...) cần đổi thì báo A.
- Không dùng `git add .` hoặc `git add -A`; luôn add đường dẫn cụ thể.
- Trước khi commit luôn chạy `git diff --cached --name-only` để kiểm tra danh sách.
