package com.backend.wms.service;

import com.backend.wms.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private static final Set<String> WRITE = Set.of("POST", "PUT", "PATCH", "DELETE");
    private final AuditLogService auditLogService;

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res,
                                Object handler, Exception ex) {
        if (ex != null || !WRITE.contains(req.getMethod())) return;
        if (res.getStatus() < 200 || res.getStatus() >= 300) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) return;

        try {
            auditLogService.record(user, req.getMethod(), req.getRequestURI(), req.getQueryString());
        } catch (Exception e) {
            log.warn("Failed to write audit log", e);
        }
    }
}
