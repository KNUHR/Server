package KNUHR.Server.user.service;

import KNUHR.Server.user.domain.User;
import KNUHR.Server.user.domain.UserDetail;
import KNUHR.Server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        log.info(userEmail);
        User user = userRepository.findByUserEmail(userEmail);
        log.info("user id : " + user.getUserId());

        List<GrantedAuthority> roles = new ArrayList<>();

        return UserDetail.builder()
                .username(user.getUserEmail())
                .password(user.getUserPassword())
                .authorities(roles)
                .build();
    }
}
