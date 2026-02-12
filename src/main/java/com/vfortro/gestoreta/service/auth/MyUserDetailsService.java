package com.vfortro.gestoreta.service.auth;

import com.vfortro.gestoreta.model.User;
import com.vfortro.gestoreta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscamos al usuario en tu tabla "users" por email
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Retornamos un objeto UserDetails que Spring Security entienda
        // Usamos la clase User de Spring Security (org.springframework.security.core.userdetails.User)
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Esta debe estar encriptada en la DB
                .roles("USER") // O el rol que tengas definido
                .build();
    }
}