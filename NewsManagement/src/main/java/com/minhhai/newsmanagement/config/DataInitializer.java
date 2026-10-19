package com.minhhai.newsmanagement.config;

import com.minhhai.newsmanagement.entity.UserAccount;
import com.minhhai.newsmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Tự động tạo tài khoản mẫu nếu bảng user_accounts còn trống
        if (userRepository.count() == 0) {
            // Tài khoản Admin
            UserAccount admin = new UserAccount();
            admin.setUsername("Admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin"));
            admin.setName("Đỗ Minh Hải");
            admin.setRole(1); // 1 = Admin
            admin.setStatus(1); // 1 = Active
            userRepository.save(admin);

            // Tài khoản Staff 1
            UserAccount staff1 = new UserAccount();
            staff1.setUsername("Staff1");
            staff1.setPasswordHash(passwordEncoder.encode("123"));
            staff1.setName("Trần Văn A");
            staff1.setRole(2); // 2 = Staff
            staff1.setStatus(1); // 1 = Active
            userRepository.save(staff1);

            // Tài khoản Staff 2
            UserAccount staff2 = new UserAccount();
            staff2.setUsername("Staff2");
            staff2.setPasswordHash(passwordEncoder.encode("123"));
            staff2.setName("Nguyễn Minh Anh");
            staff2.setRole(2); // 2 = Staff
            staff2.setStatus(1); // 1 = Active
            userRepository.save(staff2);

            System.out.println("==================================================================");
            System.out.println(" [NewsManagement] Đã tự động khởi tạo dữ liệu mẫu tài khoản:");
            System.out.println("  1. Username: Admin  | Mật khẩu: Admin (Role: Admin)");
            System.out.println("  2. Username: Staff1 | Mật khẩu: 123   (Role: Staff)");
            System.out.println("  3. Username: Staff2 | Mật khẩu: 123   (Role: Staff)");
            System.out.println("==================================================================");
        }
    }
}
