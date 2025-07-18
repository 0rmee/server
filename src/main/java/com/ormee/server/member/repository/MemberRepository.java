package com.ormee.server.member.repository;

import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);
    Optional<Member> findByUsernameAndPhoneNumber(String name, String phoneNumber);
    boolean existsByEmail(String email);

    boolean existsByUsernameAndRole(String username, Role role);
}
