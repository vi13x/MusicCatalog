package com.example.musiccatalog.repository;

import com.example.musiccatalog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
