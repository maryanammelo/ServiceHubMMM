package com.servicehub.servicehub_api.security;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

final class SecurityError {
    private SecurityError() { }
    static void write(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"error\":\"" + message + "\"}");
    }
}
