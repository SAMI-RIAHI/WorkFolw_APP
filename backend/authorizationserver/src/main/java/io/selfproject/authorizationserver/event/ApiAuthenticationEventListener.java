package io.selfproject.authorizationserver.event;

import io.selfproject.authorizationserver.model.User;
import io.selfproject.authorizationserver.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static io.selfproject.authorizationserver.utils.UserAgentUtils.*;
import static io.selfproject.authorizationserver.utils.UserUtils.getUser;

@Slf4j
@Component
@AllArgsConstructor
public class ApiAuthenticationEventListener {

    private final UserService userService;
    private final HttpServletRequest request;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        log.info("AuthenticationSuccess - {}", event);
        if (event.getAuthentication().getPrincipal() instanceof UsernamePasswordAuthenticationToken) {
            var user = getUser(event.getAuthentication());
            userService.setLastLogin(user.getUserId());
            userService.resetLoginAttempts(user.getUserUuid());
            userService.addLoginDevice(user.getUserId(), getDevice(request), getClient(request), getIpAddress(request));
        }
    }

//    @EventListener
//    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
//        log.info("AuthenticationSuccess - {}", event);
//        Authentication auth = event.getAuthentication();
//
//        if (auth instanceof UsernamePasswordAuthenticationToken) {
//            User user = getUser(auth);
//            userService.setLastLogin(user.getUserId());
//            userService.resetLoginAttempts(user.getUserUuid());
//            log.info("Device: {}, Client: {}, IP: {}", getDevice(request), getClient(request), getIpAddress(request));
//            userService.addLoginDevice(user.getUserId(), getDevice(request), getClient(request), getIpAddress(request));
//        }
//    }

    // This will not be fired because we need to explicitly trigger the authentication failure event inside the UserAuthenticationProvider.java. So I use an else condition instead in the UserAuthenticationProvider.java class.
    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        log.info("AuthenticationFailure - {}", event);
        if (event.getException() instanceof BadCredentialsException) {
            var email = (String) event.getAuthentication().getPrincipal();
            userService.updateLoginAttempts(email);
        }
    }
}
