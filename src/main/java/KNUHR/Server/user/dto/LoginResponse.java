package KNUHR.Server.user.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String email;
    private String token;

    public LoginResponse(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
