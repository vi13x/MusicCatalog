package com.example.musiccatalog.mapper;

import com.example.musiccatalog.dto.UserDTO;
import com.example.musiccatalog.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDTO toDto(User u) {
        return new UserDTO(u.getId(), u.getName());
    }
}
