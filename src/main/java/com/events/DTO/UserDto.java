package com.events.DTO;


import com.events.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.Collection;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Collection<Role> roles;

}
