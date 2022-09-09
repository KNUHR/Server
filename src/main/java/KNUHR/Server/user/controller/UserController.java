package KNUHR.Server.user.controller;

import KNUHR.Server.user.dto.LoginRequest;
import KNUHR.Server.user.dto.LoginResponse;
import KNUHR.Server.user.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        // email, passord로 토큰 생성 후 -> authenticationManager를 통해 인증
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(token);
        // 인증받은 결과를 SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // session 속성 값으로 SecurityContext 값 설정
        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        return new LoginResponse(userDetails.getUsername(), httpSession.getId());
    }
}
