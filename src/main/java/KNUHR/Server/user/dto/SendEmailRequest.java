package KNUHR.Server.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
public class SendEmailRequest {
    @NotNull
    @Email
    private String email;

    @Builder
    public SendEmailRequest(@NotNull @Email String email) {
        this.email = email;
    }

    public SendEmailRequest() {

    }
}
