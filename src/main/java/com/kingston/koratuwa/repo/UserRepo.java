package com.kingston.koratuwa.repo;

import com.kingston.koratuwa.entity.UserBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserBean, Integer> {
    Optional<UserDetails> findByUserName(String userName);
    UserBean getByUserName(String userName);

}
