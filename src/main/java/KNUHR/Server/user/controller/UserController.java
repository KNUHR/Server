package KNUHR.Server.user.controller;

import KNUHR.Server.user.dto.LoginRequest;
import KNUHR.Server.user.dto.LoginResponse;
import KNUHR.Server.user.dto.RegisterRequest;
import KNUHR.Server.user.dto.VerifyRequest;
import KNUHR.Server.user.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Objects;

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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            customUserDetailsService.registerMember(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/verify/{requestEmail}", method = RequestMethod.GET)
    public ResponseEntity<String> sendVerifyEmail(@PathVariable("requestEmail") String requestEmail) {
        try {
            customUserDetailsService.sendVerifyEmail(requestEmail);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ResponseEntity<String> verify(@RequestBody VerifyRequest verifyRequest) {
        Boolean result = customUserDetailsService.verifyEmail(verifyRequest);
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 올바르지 않습니다.");
        }
    }
}
