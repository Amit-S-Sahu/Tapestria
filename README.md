# Tapestria
Tapestria is a beautifully crafted Library Management System designed to organize, preserve, and simplify access to books and records. Just like threads woven into a tapestry, it brings together every story, every title, and every reader — into one seamless, digital experience.


Here's the **fully updated and detailed roadmap** for the Tapestria project, incorporating **all core + additional features**, aligned across development phases.

---

## 🧩 **PHASE 1: Core Setup & Auth (Done)**  
**Goal**: Establish project base, user auth, and roles.

**Includes**:
- ✅ Project structure (folders/files/docker)
- ✅ MySQL DB setup
- ✅ Spring Boot + JDBC + JPA base
- ✅ Entities: `User`, `Role`
- ✅ Auth with JWT (Login, Signup, Logout)
- ✅ Role-based access: Admin / Librarian / Student
- ✅ Activate/Deactivate users
- ✅ Environment config, app.properties

**Remaining**: None

---

## 📚 **PHASE 2: Book Management + Search/Filter**  
**Goal**: CRUD for books + student requests

**Includes**:
- 📁 `BookController`, `BookService`, `BookRepository`
- 📁 `BookRequestController` for student book requests
- Book fields: `id`, `title`, `author`, `genre`, `availabilityStatus`
- CRUD endpoints for librarians
- Students can:
  - Request new books (`POST /api/requests`)
  - View available books
- **Search/Filter**:  
  `/api/books/search?title=&author=&genre=`

---

## 📖 **PHASE 3: Issue/Return/Reissue + Auto Fine**  
**Goal**: Full borrow/return cycle

**Includes**:
- 📁 `IssuedBook` entity: tracks borrow/return
- 📁 `IssueController`, `ReturnController`, `ReissueController`
- Auto fine calculation on return:
  - `fine = max(0, daysLate × rate)`
- Overdue tracking
- Students:
  - View status
  - Reissue
- Admin:
  - View fine reports (individual, monthly)
- Notifications added in Phase 4

---

## 📬 **PHASE 4: Notifications + Email (Async)**  
**Goal**: Alert users for due dates, fines, approvals

**Includes**:
- Spring Mail (`JavaMailSender`)
- Notification entity or simple service (optional persist)
- Scheduled jobs (`@Scheduled`) for:
  - Daily overdue reminders
  - Approval status change emails
- Async with `@Async` or `ExecutorService`

---

## 📊 **PHASE 5: Admin & Librarian Dashboards**  
**Goal**: Role-specific overview panels

**Includes**:
- Expose `GET /api/dashboard/admin`, `/librarian`
- Return:
  - Total books, borrowed books
  - Monthly fine totals
  - Top borrowed books
  - Student/librarian stats
- Frontend renders interactive charts (Recharts or Chart.js)

---

## 🌟 **PHASE 6: Reviews + ML Recommendations**  
**Goal**: Engagement + smart discovery

**Includes**:
- `Review` entity (`bookId`, `studentId`, `rating`, `comment`)
- `GET/POST /api/reviews`
- Rating avg exposed in `BookDTO`
- Recommender system:
  - Python Flask app (already scaffolded)
  - Endpoint `/api/recommendations?studentId=...`
  - Backend: `RecommenderService` calls Flask API
  - Return top 5 recommended books

---

## 📦 **PHASE 7: Backup & Restore + QR Code**  
**Goal**: Data recovery + faster ops

**Includes**:
- Shell/Java utility to:
  - Dump DB (`mysqldump`)
  - Restore via `.sql` (admin-only)
- Expose endpoints:
  - `GET /api/backup`
  - `POST /api/restore`
- QR Code:
  - Frontend: `html5-qrcode` scanner for IDs
  - Backend: parse scanned ID → standard endpoints already support this

---

## ⚙️ **PHASE 8: Polish & Infra**  
**Goal**: Performance, UX, deployment

**Includes**:
- Dark mode toggle (React context)
- Docker volume mounting (data persistence)
- NGINX for reverse proxy (optional)
- Production builds
- CI/CD pipeline (GitHub Actions or Vercel/Render hooks)

---

### Final Checklist Summary

| Feature                     | Phase | Status   |
|----------------------------|--------|----------|
| Auth/Login                 | 1      | ✅ Done   |
| User Mgmt                  | 1      | ✅ Done   |
| Book CRUD + Search         | 2      | 🔜       |
| Issue/Return/Reissue       | 3      | 🔜       |
| Auto Fine Calc             | 3      | 🔜       |
| Notifications/Email        | 4      | 🔜       |
| Dashboards                 | 5      | 🔜       |
| Reviews                    | 6      | 🔜       |
| ML Recommendations         | 6      | 🔜       |
| Backup/Restore             | 7      | 🔜       |
| QR Scanner                 | 7      | 🔜       |
| Dark Mode, Infra polish    | 8      | 🔜       |

---
