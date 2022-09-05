package KNUHR.Server.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/login")
    public String getLoginForm() {
        return "login";
    }

    @GetMapping("/login/success")
    public ResponseEntity notSesstion() {
        log.info("로그인 성공");
        Map<String,Object> map = new HashMap<>();
        map.put("result", 1);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @GetMapping("/login/fail")
    public ResponseEntity hello() {
        log.info("로그인 실패");
        Map<String,Object> map = new HashMap<>();
        map.put("result", 0);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @GetMapping("/user")
    public String test(Principal user) {
        return "user만 접근";
    }
}
