package com.events.JWT;




import com.events.entity.Event;
import com.events.repositories.EventRepository;
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
import com.events.utils.JwtUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";
    private static final String USER_ID_CLAIM = "userId";
    private static final String EVENT_ID_HEADER = "eventid";
    private static final String USER_ID_HEADER = "userId";
    private static final String ROLE_ORGANIZATOR = "ROLE_ORGANIZATOR";
    private final JwtProvider jwtProvider;
    private final EventRepository eventRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
            throws IOException, ServletException, java.io.IOException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String token = getTokenFromRequest((HttpServletRequest) request);
        try {
            if (token != null && jwtProvider.validateAccessToken(token)) {
                final Claims claims = jwtProvider.getAccessClaims(token);
                final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
                jwtInfoToken.setAuthenticated(true);
                UUID userIdFromToken = getUserIdFromToken(claims);
                if (userIdFromToken != null) {
                    request.setAttribute("userId", userIdFromToken); // Сохраняем userId в атрибутах запроса
                } else {
                    log.warn("userId not found in JWT token claims");
                }
                SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
                logCurrentUserRoles(jwtInfoToken);
                if (jwtInfoToken.getRoles().stream()
                        .anyMatch(role -> role.getName().equals(ROLE_ORGANIZATOR))) {

                    UUID eventId = getEventIdFromRequest(httpRequest);
                    System.out.println("eventId = " + eventId);
                    if (eventId != null) {
                        if (!checkOrganizatorPermission(eventId, userIdFromToken)) {
                            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to access this event");
                            return;
                        }
                    }
                    UUID orgId = getOrgIdFromRequest(httpRequest);
                    System.out.println("orgId = " + orgId);
                    if (orgId != null) {
                        if (!userIdFromToken.equals(orgId)) {
                            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to access this event");
                            return;
                        }
                    }
                }
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

    private UUID getUserIdFromToken(Claims claims) {
        Object userIdObj = claims.get(USER_ID_CLAIM);
        if (userIdObj instanceof String) {
            return UUID.fromString((String) userIdObj);
        }
        return null;
    }

    private UUID getEventIdFromRequest(HttpServletRequest request) {
        String eventIdString = request.getHeader(EVENT_ID_HEADER);
        if (StringUtils.hasText(eventIdString)) {
            return UUID.fromString(eventIdString);
        }
        return null;
    }

    private UUID getOrgIdFromRequest(HttpServletRequest request) {
        String orgIdString = request.getParameter("orgId");
        if (StringUtils.hasText(orgIdString)) {
            return UUID.fromString(orgIdString);
        }
        return null;
    }

    private boolean checkOrganizatorPermission(UUID eventId, UUID userId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            return event.getOrgId().equals(userId);
        }
        return true; // Возвращаем true, если событие не найдено
    }



}