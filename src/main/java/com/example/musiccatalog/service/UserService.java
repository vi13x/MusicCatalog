package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.UserDTO;
import com.example.musiccatalog.entity.User;
import com.example.musiccatalog.exception.ErrorMessages;
import com.example.musiccatalog.exception.NotFoundException;
import com.example.musiccatalog.mapper.UserMapper;
import com.example.musiccatalog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        return UserMapper.toDto(getEntity(id));
    }

    public UserDTO create(UserDTO dto) {
        User u = new User();
        u.setName(dto.name());
        return UserMapper.toDto(userRepository.save(u));
    }

    public UserDTO update(Long id, UserDTO dto) {
        User u = getEntity(id);
        u.setName(dto.name());
        return UserMapper.toDto(userRepository.save(u));
    }

    public void delete(Long id) {
        userRepository.delete(getEntity(id));
    }

    public User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND + id));
    }
}
