package KNUHR.Server.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private String email;
    private String token;

    public LoginResponse(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
