package com.events.DTO;

import com.events.entity.Role;
import lombok.Data;


import java.util.Collection;

@Data
public class RegistrationUserDto {
    private String login;
    private String password;
    private Collection<Role> roles;
}
