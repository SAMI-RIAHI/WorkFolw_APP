package io.selfproject.authorizationserver.service.implementation;


import io.selfproject.authorizationserver.model.User;
import io.selfproject.authorizationserver.repository.UserRepository;
import io.selfproject.authorizationserver.service.UserService;
import io.selfproject.authorizationserver.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public  class UserServiceImpl implements UserService {

    private final UserRepository userRepository   ;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public void resetLoginAttempts(String userUuid) {
        userRepository.resetLoginAttempts(userUuid);
    }

    @Override
    public void updateLoginAttempts(String email) {
        userRepository.updateLoginAttempts(email);
    }

    @Override
    public void setLastLogin(Long userId) {
        userRepository.setLastLogin(userId);
    }

    @Override
    public void addLoginDevice(Long userId, String deviceName, String client, String ipAddress) {
        userRepository.addLoginDevice(userId, deviceName, client, ipAddress);
    }

    @Override
    public Boolean verifyQrCode(String userUuid, String code) {
        var user = userRepository.getUserByUuid(userUuid);
        return UserUtils.verifyQrCode(user.getQrCodeSecret(), code);
    }
}
