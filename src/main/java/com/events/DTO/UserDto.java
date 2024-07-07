package com.events.DTO;


import com.events.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String password;
    private Collection<Role> roles;

}
