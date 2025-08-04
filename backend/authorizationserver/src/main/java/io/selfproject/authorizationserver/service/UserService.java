package io.selfproject.authorizationserver.service;

import io.selfproject.authorizationserver.model.User;


public interface UserService {

    User getUserByEmail(String email);
    void resetLoginAttempts (String userUuid);
    void updateLoginAttempts (String email);
    void setLastLogin(Long userId);
    void addLoginDevice (Long userId, String deviceName, String client, String ipAddress);
    Boolean verifyQrCode (String userUuid, String code);
}
