package io.selfproject.discoveryservice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.util.Assert;

import java.io.IOException;

public class DiscoveryAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {


@Override
public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    response.setHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    var writer = response.getWriter();
    writer.println("HTTP Status 401 - You Are Not Logged In");
}

@Override
public void afterPropertiesSet() {
      setRealmName("myrealm");
      super.afterPropertiesSet();
}




}
