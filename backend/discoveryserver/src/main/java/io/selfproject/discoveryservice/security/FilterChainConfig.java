package io.selfproject.discoveryservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static io.selfproject.discoveryservice.constants.Roles.APP_READ;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class FilterChainConfig {

    private final DiscoveryUserDetailsService userDetailsService ;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/eureka/**"))
                        .userDetailsService(userDetailsService)
                .exceptionHandling(exception ->exception.accessDeniedHandler(new DiscoveryAccessDeniedHandler()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/eureka/font/**", "/eureka/css/**", "/eureka/js/**","/eureka/images/**","/icon/**").permitAll()
                        .requestMatchers("/eureka/**").hasAuthority(APP_READ)
                         .requestMatchers("/**").hasAuthority(APP_READ)
                        .anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(new DiscoveryAuthenticationEntryPoint()));

        return http.build();
    }


}
