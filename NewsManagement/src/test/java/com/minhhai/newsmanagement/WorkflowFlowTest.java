package com.minhhai.newsmanagement;

import com.minhhai.newsmanagement.scheduler.PublishScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Chạy trọn luồng biên tập qua HTTP thật (H2 in-memory, cấu hình ở src/test/resources). */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkflowFlowTest {

    @LocalServerPort int port;
    @Autowired PublishScheduler scheduler;

    final HttpClient http = HttpClient.newHttpClient();
    final JsonMapper json = JsonMapper.builder().build();

    record Res(int status, JsonNode body) {}

    Res call(String method, String path, String token, Object body) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1" + path))
                .header("Content-Type", "application/json");
        if (token != null) b.header("Authorization", "Bearer " + token);
        b.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
        HttpResponse<String> r = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        return new Res(r.statusCode(), r.body().isBlank() ? null : json.readTree(r.body()));
    }

    String login(String user, String pass) throws Exception {
        Res r = call("POST", "/auth/login", null, Map.of("username", user, "password", pass));
        assertEquals(200, r.status(), "login " + user);
        return r.body().get("token").asString();
    }

    long draft(String token, String title, String content, Long categoryId) throws Exception {
        Map<String, Object> m = new java.util.HashMap<>();
        m.put("title", title);
        m.put("content", content);
        m.put("categoryId", categoryId);
        Res r = call("POST", "/news", token, m);
        assertEquals(201, r.status());
        assertEquals("DRAFT", r.body().get("status").asString());
        return r.body().get("id").asLong();
    }

    List<String> actions(String token, long id) throws Exception {
        Res r = call("GET", "/news/" + id + "/audit-logs", token, null);
        assertEquals(200, r.status());
        List<String> out = new ArrayList<>();
        r.body().forEach(n -> out.add(n.get("action").asString()));
        return out;
    }

    boolean publicHas(long id) throws Exception {
        Res r = call("GET", "/public/news", null, null);
        boolean found = false;
        for (JsonNode n : r.body()) if (n.get("id").asLong() == id) found = true;
        return found;
    }

    @Test
    void fullEditorialFlow() throws Exception {
        String c1 = login("contributor1", "123456");
        String c2 = login("contributor2", "123456");
        String ed = login("editor", "editor123");
        String ad = login("admin", "admin123");

        long cat = call("GET", "/categories", null, null).body().get(0).get("id").asLong();

        // --- phân quyền cơ bản ---
        assertEquals(401, call("GET", "/news/my", null, null).status());
        assertEquals(403, call("GET", "/editorial/queue", c1, null).status());
        assertEquals(403, call("GET", "/users", ed, null).status());
        assertEquals(403, call("POST", "/news", ed, Map.of("title", "x")).status());

        // --- Draft: thiếu nội dung thì không gửi duyệt được ---
        long a1 = draft(c1, "Lịch thi cuối kỳ", null, null);
        assertEquals(400, call("POST", "/news/" + a1 + "/submit", c1, null).status());
        assertEquals(403, call("PUT", "/news/" + a1, c2, Map.of("title", "hack")).status());
        Res upd = call("PUT", "/news/" + a1, c1, Map.of("title", "Lịch thi cuối kỳ", "content", "Nội dung tiếng Việt có dấu", "categoryId", cat));
        assertEquals(200, upd.status());
        assertEquals("Nội dung tiếng Việt có dấu", upd.body().get("content").asString());
        // người khác không gửi duyệt hộ được
        assertEquals(403, call("POST", "/news/" + a1 + "/submit", c2, null).status());
        // Draft không lộ ra ngoài
        assertFalse(publicHas(a1));
        assertEquals(404, call("GET", "/public/news/" + a1, null, null).status());
        assertEquals(403, call("GET", "/editorial/" + a1, ed, null).status());

        // --- Submit: khóa sửa ---
        Res sub = call("POST", "/news/" + a1 + "/submit", c1, null);
        assertEquals(200, sub.status());
        assertEquals("PENDING_REVIEW", sub.body().get("status").asString());
        assertEquals(409, call("PUT", "/news/" + a1, c1, Map.of("title", "sửa lén")).status());
        assertEquals(409, call("POST", "/news/" + a1 + "/submit", c1, null).status());

        // --- Editor: queue, yêu cầu sửa bắt buộc có feedback ---
        Res q = call("GET", "/editorial/queue", ed, null);
        assertEquals(200, q.status());
        assertEquals(a1, q.body().get(0).get("id").asLong());
        assertEquals(400, call("POST", "/editorial/" + a1 + "/request-changes", ed, Map.of("feedback", " ")).status());
        Res rc = call("POST", "/editorial/" + a1 + "/request-changes", ed, Map.of("feedback", "Bổ sung phòng thi"));
        assertEquals("CHANGES_REQUESTED", rc.body().get("status").asString());
        assertEquals("Bổ sung phòng thi", rc.body().get("editorFeedback").asString());
        assertEquals(1, call("GET", "/news/my/changes-count", c1, null).body().get("count").asLong());

        // --- Tác giả sửa và nộp lại ---
        assertEquals(200, call("PUT", "/news/" + a1, c1, Map.of("title", "Lịch thi cuối kỳ", "content", "Đã bổ sung phòng thi", "categoryId", cat)).status());
        assertEquals("PENDING_REVIEW", call("POST", "/news/" + a1 + "/resubmit", c1, null).body().get("status").asString());
        assertEquals(0, call("GET", "/news/my/changes-count", c1, null).body().get("count").asLong());

        // --- Hẹn giờ: giờ quá khứ bị từ chối; hợp lệ thì chờ Scheduler ---
        assertEquals(400, call("POST", "/editorial/" + a1 + "/approve", ed, Map.of("scheduledPublishDate", Instant.now().minusSeconds(60).toString())).status());
        Res sch = call("POST", "/editorial/" + a1 + "/approve", ed, Map.of("scheduledPublishDate", Instant.now().plusSeconds(2).toString()));
        assertEquals("APPROVED_SCHEDULED", sch.body().get("status").asString());
        assertEquals(409, call("PUT", "/news/" + a1, c1, Map.of("title", "sửa lén")).status()); // vẫn khóa
        scheduler.publishDueArticles();
        assertFalse(publicHas(a1), "chưa đến giờ thì chưa đăng");
        Thread.sleep(2300);
        scheduler.publishDueArticles();
        assertTrue(publicHas(a1), "đến giờ thì tự đăng");
        assertEquals("Lịch thi cuối kỳ", call("GET", "/public/news/" + a1, null, null).body().get("title").asString());
        assertNull(call("GET", "/public/news/" + a1, null, null).body().get("editorFeedback").asString(null), "public không lộ feedback");

        // --- Audit đầy đủ, kể cả Scheduler (actor trống) ---
        assertEquals(List.of("CREATE_DRAFT", "SUBMIT", "REQUEST_CHANGES", "RESUBMIT", "SCHEDULE", "AUTO_PUBLISH"), actions(c1, a1));
        Res logs = call("GET", "/news/" + a1 + "/audit-logs", ed, null);
        assertTrue(logs.body().get(5).get("actorId").isNull());
        assertEquals(403, call("GET", "/news/" + a1 + "/audit-logs", c2, null).status());

        // --- Đăng ngay + Archive ---
        long a2 = draft(c1, "Tuyển sinh 2027", "Mở đơn từ tháng 3", cat);
        assertEquals(409, call("POST", "/editorial/" + a2 + "/approve", ed, Map.of()).status(), "DRAFT không duyệt thẳng được");
        call("POST", "/news/" + a2 + "/submit", c1, null);
        assertEquals(403, call("POST", "/editorial/" + a2 + "/approve", ad, Map.of()).status(), "Admin không duyệt qua API thường");
        assertEquals("PUBLISHED", call("POST", "/editorial/" + a2 + "/approve", ed, null).body().get("status").asString());
        assertTrue(publicHas(a2));
        assertEquals("ARCHIVED", call("POST", "/editorial/" + a2 + "/archive", ed, Map.of("reason", "Hết hạn")).body().get("status").asString());
        assertFalse(publicHas(a2));
        assertEquals(409, call("POST", "/editorial/" + a2 + "/archive", ed, Map.of()).status(), "ARCHIVED là trạng thái cuối");
        assertEquals(List.of("CREATE_DRAFT", "SUBMIT", "APPROVE_NOW", "ARCHIVE"), actions(c1, a2));

        // --- Archive bài đang hẹn giờ thì Scheduler không đăng ---
        long a3 = draft(c1, "Sự kiện hẹn giờ", "Nội dung", cat);
        call("POST", "/news/" + a3 + "/submit", c1, null);
        call("POST", "/editorial/" + a3 + "/approve", ed, Map.of("scheduledPublishDate", Instant.now().plusSeconds(1).toString()));
        assertEquals("ARCHIVED", call("POST", "/editorial/" + a3 + "/archive", ed, Map.of()).body().get("status").asString());
        Thread.sleep(1300);
        scheduler.publishDueArticles();
        assertFalse(publicHas(a3));

        // --- Admin override: bắt buộc lý do, có log ---
        assertEquals(403, call("POST", "/admin/news/" + a2 + "/override", ed, Map.of("targetStatus", "DRAFT", "reason", "x")).status());
        assertEquals(400, call("POST", "/admin/news/" + a2 + "/override", ad, Map.of("targetStatus", "PUBLISHED", "reason", " ")).status());
        assertEquals(400, call("POST", "/admin/news/" + a2 + "/override", ad, Map.of("targetStatus", "APPROVED_SCHEDULED", "reason", "r")).status(), "hẹn giờ cần giờ tương lai");
        Res ov = call("POST", "/admin/news/" + a2 + "/override", ad, Map.of("targetStatus", "PUBLISHED", "reason", "Đăng lại theo chỉ đạo"));
        assertEquals("PUBLISHED", ov.body().get("status").asString());
        assertTrue(publicHas(a2));
        assertEquals("OVERRIDE", actions(c1, a2).get(4));

        // --- Users/Categories chỉ Admin, không lộ passwordHash, khóa tài khoản có hiệu lực ngay ---
        Res users = call("GET", "/users", ad, null);
        assertEquals(200, users.status());
        assertFalse(users.body().toString().contains("passwordHash"));
        long uid = call("POST", "/users", ad, Map.of("username", "tmp", "password", "abc123", "name", "Tạm", "role", "CONTRIBUTOR")).body().get("id").asLong();
        String tmp = login("tmp", "abc123");
        assertEquals(200, call("GET", "/auth/me", tmp, null).status());
        assertEquals(200, call("PUT", "/users/" + uid, ad, Map.of("username", "tmp", "name", "Tạm", "role", "CONTRIBUTOR", "status", 0)).status());
        assertEquals(401, call("GET", "/auth/me", tmp, null).status(), "token cũ của tài khoản bị khóa phải bị từ chối");
        assertEquals(403, call("POST", "/auth/login", null, Map.of("username", "tmp", "password", "abc123")).status());
        assertEquals(403, call("POST", "/categories", ed, Map.of("name", "X")).status());
        // category đang dùng thì vô hiệu hóa chứ không xóa
        assertTrue(call("DELETE", "/categories/" + cat, ad, null).body().get("message").asString().contains("vô hiệu hóa"));
        assertEquals(400, call("POST", "/news", c1, Map.of("title", "Dùng category đã khóa", "categoryId", cat)).status());
    }
}
