package io.getarrays.notificationservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

/**
 * @author Junior RT
 * @version 1.0
 * @license Get Arrays, LLC (<a href="https://www.getarrays.io">Get Arrays, LLC</a>)
 * @email getarrayz@gmail.com
 * @since 1/28/25
 */

@Component
public class JwtConverter implements Converter<Jwt, JwtAuthenticationToken> {
    private static final String AUTHORITY_KEY = "authorities";

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        var claims = (String) jwt.getClaims().get(AUTHORITY_KEY);
        var authorities = commaSeparatedStringToAuthorityList(claims);
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}