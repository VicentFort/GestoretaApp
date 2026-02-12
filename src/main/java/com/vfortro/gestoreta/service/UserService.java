package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.UserCreateDto;
import com.vfortro.gestoreta.dto.UserUpdateDto;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConversor userConversor;

    @Transactional
    public UserCreateDto createUser(UserCreateDto user) {
        User saved = userRepository.save(userConversor.fromDto2Entity(user));
        return userConversor.fromEntity2Dto(saved);
    }

    @Transactional(readOnly = true)
    public UserCreateDto readUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) return null;
        return userConversor.fromEntity2Dto(user);
    }
    @Transactional
    public UserCreateDto updateUser(UserUpdateDto newUser, Long userId) {
        User updatedUser = userRepository.findById(userId).map(
                user -> {
                    if(newUser.getName() != null) user.setName(newUser.getName());
                    if(newUser.getSurname() != null) user.setSurname(newUser.getSurname());
                    if(newUser.getBirthday() != null) user.setBirthday(newUser.getBirthday());
                    if(newUser.getShowBday() != null) user.setShowBday(newUser.getShowBday());
                    if(newUser.getUrlPfp() != null) user.setUrlPfp(newUser.getUrlPfp());
                    return userRepository.saveAndFlush(user);
                }
        ).orElse(null);
        if (updatedUser != null) {
            return userConversor.fromEntity2Dto(updatedUser);
        }
        return null;
    }
}
