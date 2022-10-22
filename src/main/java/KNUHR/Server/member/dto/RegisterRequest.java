package KNUHR.Server.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class RegisterRequest {

    @NotNull
    private String userName;

    @NotNull
    private String nickName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private boolean verified;

    @Builder
    public RegisterRequest(@NotNull String userName, @NotNull String nickName, @NotNull @Email String email, @NotNull String password, @NotNull boolean verified) {
        this.userName = userName;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.verified = verified;
    }
}
