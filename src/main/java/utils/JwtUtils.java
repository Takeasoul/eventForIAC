package utils;


import JWT.JwtAuthentication;
import entity.Role;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;



import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<Role> getRoles(Claims claims) {
        List<Map<String, String>> rolesList = claims.get("roles", List.class);
        if (rolesList == null) {
            return Collections.emptySet();
        }

        Set<Role> roles = new HashSet<>();
        for (Map<String, String> roleMap : rolesList) {
            String roleName = roleMap.get("name");
            if (roleName != null) {
                Role role = new Role();
                role.setName(roleName);
                roles.add(role);
            }
        }

        return roles;
    }

}

