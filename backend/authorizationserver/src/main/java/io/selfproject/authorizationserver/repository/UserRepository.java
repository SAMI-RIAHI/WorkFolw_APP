package io.selfproject.authorizationserver.repository;

import io.selfproject.authorizationserver.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User getUserByUuid(String userUuid);
    User getUserByEmail(String email);
    void resetLoginAttempts(String userUuid);
    void updateLoginAttempts(String email);
    void setLastLogin(Long userId);
    void addLoginDevice (Long userId, String deviceName, String client, String ipAddress);
}
