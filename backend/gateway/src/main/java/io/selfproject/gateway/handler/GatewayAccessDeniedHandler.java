package io.selfproject.gateway.handler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;

import static io.selfproject.gateway.utils.RequestUtils.handleErrorResponse;

@Component
public class GatewayAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        handleErrorResponse(request,response,accessDeniedException);
    }
}
