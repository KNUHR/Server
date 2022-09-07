package KNUHR.Server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class FormFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // api/login/fail로 리다이렉트
        log.info("Login Fail..");
        String errorMsg = "Invalid";

        setDefaultFailureUrl("/login?error=true&exception="+errorMsg);
        super.onAuthenticationFailure(request, response, exception);
        //response.sendRedirect("http://localhost:8080/api/login/fail");
    }
}
