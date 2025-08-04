package io.selfproject.authorizationserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long userId ;
    private String userUuid;
    private String firstName ;
    private String lastName ;
    private String email ;
    private String password;
    private String phone;
    private String bio;
    private String imageUrl;
    private String qrCodeImageUrl;
    private String qrCodeSecret ;
    private String lastLogin ;
    private String address ;
    private int loginAttempts;
    private String createdAt ;
    private String updatedAt ;
    private String role ;
    private String authorities ;
    private boolean mfa ;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

}
