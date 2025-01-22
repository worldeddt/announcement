package api.announcement.controller.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateRequestDto {
    private String username;
    private String email;
    private String password;
}
