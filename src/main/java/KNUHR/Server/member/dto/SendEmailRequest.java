package KNUHR.Server.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class SendEmailRequest {
    @NotNull
    @Email
    private String email;

    @Builder
    public SendEmailRequest(@NotNull @Email String email) {
        this.email = email;
    }
}
