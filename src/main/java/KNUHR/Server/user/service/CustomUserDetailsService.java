package KNUHR.Server.user.service;

import KNUHR.Server.user.domain.Member;
import KNUHR.Server.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

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
