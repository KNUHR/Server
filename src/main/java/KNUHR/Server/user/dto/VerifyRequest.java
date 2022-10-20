package KNUHR.Server.user.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
public class VerifyRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String verifyCode;

    public VerifyRequest(@NotNull @Email String email, @NotNull String verifyCode) {
        this.email = email;
        this.verifyCode = verifyCode;
    }
}
