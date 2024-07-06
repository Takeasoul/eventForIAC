package JWT;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import utils.JwtUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
            throws IOException, ServletException, java.io.IOException {
        final String token = getTokenFromRequest((HttpServletRequest) request);
        try {
        if (token != null && jwtProvider.validateAccessToken(token)) {
            final Claims claims = jwtProvider.getAccessClaims(token);
            final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            logCurrentUserRoles(jwtInfoToken);
        }
        fc.doFilter(request, response);
    } catch (ExpiredJwtException e) {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "JWT expired: " + e.getMessage());
    }
}

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void logCurrentUserRoles(JwtAuthentication jwtInfoToken) {
        if (jwtInfoToken != null && jwtInfoToken.getAuthorities() != null) {
            jwtInfoToken.getAuthorities().forEach(authority -> {
                log.info("Current user role: {}", authority.getAuthority());
            });
        }
    }
}