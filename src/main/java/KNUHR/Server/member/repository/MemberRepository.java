package KNUHR.Server.member.repository;

import KNUHR.Server.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    @Transactional
    Optional<Member> findByMemberEmail(String memberEmail);

    boolean existsByMemberEmail(String memberEmail);
}
