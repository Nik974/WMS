package com.backend.wms.service;

import com.backend.wms.dto.AuditLogDto;
import com.backend.wms.entity.AuditLog;
import com.backend.wms.entity.User;
import com.backend.wms.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository repository;

    public void record(User user, String method, String path, String query) {
        String[] seg = path.replaceFirst("^/api/v\\d+/", "").split("/");
        int i = (seg[0].equals("admin") && seg.length > 1) ? 1 : 0;

        Long resourceId = null;
        String action = null;
        for (int j = i + 1; j < seg.length; j++) {
            if (seg[j].matches("\\d+")) resourceId = Long.valueOf(seg[j]);
            else action = seg[j];
        }

        AuditLog log = new AuditLog();
        log.setCreatedAt(LocalDateTime.now());
        log.setUserId(user.getId());
        log.setUsername(user.getUsername());
        log.setEmail(user.getEmail());
        log.setHttpMethod(method);
        log.setResource(seg[i]);
        log.setResourceId(resourceId);
        log.setAction(action);
        log.setQuery(query == null ? null : query.substring(0, Math.min(query.length(), 300)));
        log.setPath(path.substring(0, Math.min(path.length(), 300)));
        repository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> search(LocalDate from, LocalDate to, String user, String resource, Pageable pageable) {
        LocalDateTime f = (from == null ? LocalDate.of(2000, 1, 1) : from).atStartOfDay();
        LocalDateTime t = (to == null ? LocalDate.now().plusYears(1) : to.plusDays(1)).atStartOfDay();
        return repository.search(f, t, user == null ? "" : user.trim(),
                        resource == null ? "" : resource.trim(), pageable)
                .map(a -> new AuditLogDto(a.getId(), a.getCreatedAt(), a.getUsername(), a.getEmail(),
                        a.getHttpMethod(), a.getResource(), a.getResourceId(), a.getAction(), a.getQuery()));
    }
}
