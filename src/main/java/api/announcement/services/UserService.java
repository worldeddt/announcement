package api.announcement.services;


import api.announcement.controller.dto.UserCreateRequestDto;
import api.announcement.controller.dto.UserUpdateRequestDto;
import api.announcement.entities.User;
import api.announcement.enums.Role;
import api.announcement.enums.UserStatus;
import api.announcement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static api.announcement.exception.ErrorCode.DELETED_USER;
import static api.announcement.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public User createUser(UserCreateRequestDto userCreateRequestDto) {
        User user = new User();
        user.setUsername(userCreateRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userCreateRequestDto.getPassword()));
        user.setEmail(userCreateRequestDto.getEmail());
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.valueOf(userCreateRequestDto.getRole()));
        userRepository.save(user);
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream().filter(user -> user.getStatus() == UserStatus.ACTIVE).toList();
    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NOT_FOUND_USER::build);
        if (user.getStatus().equals(UserStatus.DELETED)) throw DELETED_USER.build();
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(Long id, UserUpdateRequestDto userDetails) {
        User user = getUserById(id); // Check if user exists
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setRole(Role.valueOf(userDetails.getRole()));
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = getUserById(id); // Check if user exists
        user.setStatus(UserStatus.DELETED);
    }
}
