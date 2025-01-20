package api.announcement.components;

import api.announcement.entities.User;
import api.announcement.enums.Role;
import api.announcement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

//        if (!userRepository.findByUsername("admin")
//                .isEmpty()) {
//            return;
//        }
//
//        User user = new User();
//        user.setUsername("admin");
//        user.setPassword(passwordEncoder.encode("user123"));
//        user.setEmail("user@example.com");
//        user.setRole(Role.ADMIN);
//        userRepository.save(user);
    }
}