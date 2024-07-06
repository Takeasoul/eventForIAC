package com.events.service;

import com.events.DTO.RegistrationUserDto;
import com.events.repositories.RoleRepository;
import com.events.entity.User;
import com.events.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;


    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByLogin(String login){
        return userRepository.findUserByLogin(login);
    }

    public Optional<User> findById(Long id){
        return userRepository.findUserById(id);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public User createNewUser(RegistrationUserDto registrationUserDto){
        User user = new User();
        user.setLogin(registrationUserDto.getLogin());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(registrationUserDto.getRoles());
        userRepository.save(user);
        return user;
    }



    public ResponseEntity<?> deleteUser(Long id)
    {
        userRepository.deleteById(id);
        return ResponseEntity.ok("ВСЕ ГУД");
    }


}
