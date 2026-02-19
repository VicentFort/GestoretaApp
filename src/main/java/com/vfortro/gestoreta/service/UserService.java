package com.vfortro.gestoreta.service;

import java.time.LocalDate;

import com.vfortro.gestoreta.conversor.UserConversor;
import com.vfortro.gestoreta.dto.fallas.FallaInfoDto;
import com.vfortro.gestoreta.dto.users.UserCreateDto;
import com.vfortro.gestoreta.dto.users.UserUpdateDto;
import com.vfortro.gestoreta.model.Position;
import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConversor userConversor;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserCreateDto createUser(UserCreateDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(userConversor.fromDto2Entity(user));
        return userConversor.fromEntity2Dto(saved);
    }

    @Transactional(readOnly = true)
    public UserCreateDto readUser(String email) {
        User user = (User) userRepository.findByEmail(email).orElse(null);
        if(user == null) return null;
        return userConversor.fromEntity2Dto(user);
    }

    @Transactional(readOnly = true)
    public UserCreateDto readUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) return null;
        return userConversor.fromEntity2Dto(user);
    }

    @Transactional
    public UserCreateDto updateUser(UserUpdateDto newUser, Long userId, String email) throws AccessDeniedException {
        if(email != userRepository.findUserById(userId).getEmail()) throw new AccessDeniedException("Sin permiso.");
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

    @Transactional(readOnly = true)
    public boolean checkAdminAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getAdminAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkBankAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getBankAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkLotteryAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getLotteryAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkArtsAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getArtsAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkPyrothecnicsAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getPyrotechnicsAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkHouseHoldAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getHouseholdAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkOtherAccess(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getPositions().isEmpty()) return false;
        for(Position position : user.getPositions()) {
            if(position.getOtherAccess()) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public UserCreateDto readUserSecure( Long userId, String email) throws AccessDeniedException {
        UserCreateDto user = readUser(userId);
        if(user == null) return null;
        if(!Objects.equals(user.getUserId(), readUser(email).getUserId())) throw new AccessDeniedException("Sin permiso.");
        return user;
    }

    @Transactional(readOnly = true)
    public String getFallaName(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getFalla() == null) return "";
        return user.getFalla().getName();
    }

    @Transactional(readOnly = true)
    public Long getFallaId(String email) {
        User user = userRepository.findUserByEmail(email);
        if(user.getFalla() == null) return 0L;
        return user.getFalla().getId();
    }

    @Transactional(readOnly = true)
    public FallaInfoDto getFallaInfo(String email) {
        User user =userRepository.findUserByEmail(email);
        if(user.getFalla() == null) return new FallaInfoDto(0L, "INEXISTENTE", LocalDate.now());
        FallaInfoDto result = new FallaInfoDto();
        result.setFallaId(user.getFalla().getIdFalla());
        result.setName(user.getFalla().getName());
        result.setCreationDate(user.getFalla().getCreationDate());
        return result;
    }
}
