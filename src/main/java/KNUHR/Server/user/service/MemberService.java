package KNUHR.Server.user.service;

import KNUHR.Server.config.BcryptPasswordEncoder;
import KNUHR.Server.user.domain.Member;
import KNUHR.Server.user.dto.RegisterRequest;
import KNUHR.Server.user.dto.VerifyRequest;
import KNUHR.Server.user.repository.MemberRepository;
import KNUHR.Server.user.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;
    private final EmailService emailService;
    private final BcryptPasswordEncoder bcryptPasswordEncoder;

    public void registerMember(RegisterRequest registerRequest) throws IllegalArgumentException, IllegalStateException {
        if(!registerRequest.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 올바르지 않습니다.");
        }
        if(memberRepository.existsByMemberEmail(registerRequest.getEmail())) {
            throw new IllegalStateException("이미 가입한 이메일입니다.");
        }
        Member member = Member.builder()
                .memberId(UUID.randomUUID())
                .memberName(registerRequest.getUserName())
                .memberNickname(registerRequest.getNickName())
                .memberEmail(registerRequest.getEmail())
                .memberPassword(bcryptPasswordEncoder.getPasswordEncoder().encode(registerRequest.getPassword()))
                .build();
        memberRepository.save(member);
    }

    public void sendVerifyEmail(String email) {
        String code = createCode();
        redisUtil.setDataExpire(code, email, 60 * 5L);
        emailService.sendMail(email, "[KNUHR] 회원가입 인증메일입니다.", code);
    }

    public Boolean verifyEmail(VerifyRequest verifyRequest) throws  IllegalStateException {
        String email = redisUtil.getData(verifyRequest.getVerifyCode());
        if(email == null) throw new IllegalStateException("인증번호가 올바르지 않습니다.");
        if(email.equals(verifyRequest.getEmail())) {
            redisUtil.deleteData(verifyRequest.getVerifyCode());
            return Boolean.TRUE;
        }
        else {
            throw new IllegalStateException("인증번호가 올바르지 않습니다.");
        }
    }

    public String createCode() {
        StringBuffer code = new StringBuffer();
        Random rand = new Random();

        for (int i = 0; i < 6; i++) {
            int randomCase = rand.nextInt(2);
            switch (randomCase) {
                case 0:
                    // 대문자 A-Z
                    code.append((char)(rand.nextInt(26) + 65));
                    break;
                case 1:
                    // 숫자 0-9
                    code.append(rand.nextInt(10));
                    break;
            }
        }
        return code.toString();
    }

    @Override
    public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
        // Email로 member 검색
        Member member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(EntityNotFoundException::new);
        // ROLE_USER 권한 부여
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return User.builder()
                .username(member.getMemberEmail())
                .password(member.getMemberPassword())
                .authorities(authority)
                .build();
    }
}
