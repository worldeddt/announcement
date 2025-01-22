package api.announcement.controller.dto;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDto {
    private String username;
    private String email;
    private String password;
    private String role;
}
