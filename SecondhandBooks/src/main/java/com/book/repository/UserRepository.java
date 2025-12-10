package com.book.repository;

import com.book.model.User;
import org.springframework.data.repository.ListCrudRepository;
import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Long> {
    // 透過帳號查詢使用者 (用於登入檢查)
    Optional<User> findByAccount(String account);

    // 檢查帳號是否存在 (用於註冊檢查)
    boolean existsByAccount(String account);
}