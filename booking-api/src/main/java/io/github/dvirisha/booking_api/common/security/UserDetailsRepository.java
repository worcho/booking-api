package io.github.dvirisha.booking_api.common.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
