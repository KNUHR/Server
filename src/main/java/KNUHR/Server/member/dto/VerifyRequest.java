package KNUHR.Server.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class VerifyRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String verifyCode;

    @Builder
    public VerifyRequest(@NotNull @Email String email, @NotNull String verifyCode) {
        this.email = email;
        this.verifyCode = verifyCode;
    }
}
