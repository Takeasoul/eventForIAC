package com.events.JWT;

import lombok.Data;

@Data
public class JwtRequest {
    private String login;
    private String password;
}
