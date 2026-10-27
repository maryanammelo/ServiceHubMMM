package com.servicehub.servicehub_api.exception;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    ResponseEntity<Map<String,Object>> authentication(Exception ex) {
        return ResponseEntity.status(401).body(Map.of("status",401,"error","Credenciais inválidas"));
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    ResponseEntity<Map<String,Object>> denied(Exception ex) {
        return ResponseEntity.status(403).body(Map.of("status",403,"error","Acesso negado"));
    }
    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
        jakarta.validation.ConstraintViolationException.class})
    ResponseEntity<Map<String,Object>> malformed(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("status",400,"error","Requisição inválida"));
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    ResponseEntity<Map<String,Object>> integrity(Exception ex) {
        return ResponseEntity.status(409).body(Map.of("status",409,"error","Dados em conflito com um registro existente"));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage(), (a,b) -> a));
        return ResponseEntity.badRequest().body(Map.of("status",400,"error","Dados inválidos","fields",errors));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String,Object>> conflict(IllegalArgumentException ex) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status",409,"error",ex.getMessage())); }
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    ResponseEntity<Map<String,Object>> status(org.springframework.web.server.ResponseStatusException ex) { return ResponseEntity.status(ex.getStatusCode()).body(Map.of("status",ex.getStatusCode().value(),"error",ex.getReason())); }
}
