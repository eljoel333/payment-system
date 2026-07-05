package com.paymentystem.auth.infrastructure.adapter.input.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    private static final String LOG_PATH = "logs/auth-service";

    // Solo ADMIN puede descargar logs
    @GetMapping("/today")
    //@PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadToday() {
        return downloadByDate(LocalDate.now());
    }

    @GetMapping("/{date}")
    //@PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        String fileName = "auth-service." + date.format(DateTimeFormatter.ISO_DATE) + ".log";
        File logFile = new File(LOG_PATH + "/" + fileName);

        // Si es el día de hoy, sirve el archivo activo
        if (date.equals(LocalDate.now())) {
            logFile = new File(LOG_PATH + "/auth-service.log");
            fileName = "auth-service-" + LocalDate.now() + ".log";
        }

        if (!logFile.exists()) {
            log.warn("Log no encontrado: {}", logFile.getPath());
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(logFile);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/list")
    //@PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<?> list() {
        File logDir = new File(LOG_PATH);
        if (!logDir.exists() || !logDir.isDirectory()) {
            return ResponseEntity.ok(java.util.List.of());
        }

        var files = java.util.Arrays.stream(logDir.listFiles())
                .filter(f -> f.getName().endsWith(".log"))
                .map(f -> java.util.Map.of(
                        "name", f.getName(),
                        "size", f.length() + " bytes",
                        "lastModified", new java.util.Date(f.lastModified()).toString()
                ))
                .toList();

        return ResponseEntity.ok(files);
    }
}