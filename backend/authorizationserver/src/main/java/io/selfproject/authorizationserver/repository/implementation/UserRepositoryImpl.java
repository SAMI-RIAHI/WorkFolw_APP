package io.selfproject.authorizationserver.repository.implementation;

import io.selfproject.authorizationserver.exception.ApiException;
import io.selfproject.authorizationserver.model.User;
import io.selfproject.authorizationserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.selfproject.authorizationserver.query.UserQuery.*;
import static java.lang.String.format;
import static java.util.Map.of;

@Slf4j
@Service
@RequiredArgsConstructor
public  class UserRepositoryImpl implements UserRepository {
    //Database Calls here

    private final JdbcClient jdbcClient;

    @Override
    public User getUserByUuid(String userUuid) {
        try{
            return jdbcClient.sql(SELECT_USER_BY_USER_UUID_QUERY).param("userUuid", userUuid).query(User.class).single();
        } catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with UUID %s not found",userUuid));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An Error Occurred While Fetching User, Please try again later.");
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try{
            return jdbcClient.sql(SELECT_USER_BY_EMAIL_QUERY).param("email", email).query(User.class).single();
        } catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with Email %s not found", email));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An Error Occurred While Fetching User, Please try again later.");
        }
    }

    @Override
    public void resetLoginAttempts(String userUuid) {
        try {
            jdbcClient.sql(RESET_LOGIN_ATTEMPTS_QUERY).param("userUuid", userUuid).update();
        } catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with UUID %s not found", userUuid));
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("Failed to reset login attempts. Please try again later.");
        }
    }

    @Override
    public void updateLoginAttempts(String email) {
        try {
            jdbcClient.sql(UPDATE_LOGIN_ATTEMPTS_QUERY).param("email", email).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", email));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred while updating login attempts for email %s");
        }
    }

    @Override
    public void setLastLogin(Long userId) {
        try {
            jdbcClient.sql(SET_LAST_LOGIN_QUERY).param("userId", userId).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with ID %d not found", userId));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred while setting last login for user ...");
        }
    }

//    @Override
//    public void addLoginDevice(Long userId, String device, String client, String ipAddress) {
//        try {
//            jdbcClient.sql(INSERT_NEW_DEVICE_QUERY).params(of("userId", userId, "device", device, "client", client, "ipAddress", ipAddress)).update();
//        } catch (EmptyResultDataAccessException exception) {
//            throw new ApiException(format("No user found by ID %s", userId));
//        } catch (Exception exception) {
//            throw new ApiException("An error occurred. Please try again.");
//        }
//    }

    @Override
    public void addLoginDevice(Long userId, String device, String client, String ipAddress) {
        try {
            // Vérifier si le device existe déjà pour cet utilisateur
            Long deviceId = jdbcClient.sql(CHECK_DEVICE_EXIST_QUERY)
                    .params(of("userId", userId,"device", device,"client", client,"ipAddress", ipAddress))
                    .query(Long.class).optional().orElse(null);

            if (deviceId != null) {
                // Mise à jour du timestamp si le device existe déjà
                jdbcClient.sql(UPDATE_DEVICE_TIMESTAMP_QUERY).params(of("deviceId", deviceId)).update();
            } else {
                // Insertion si le device est nouveau
                jdbcClient.sql(INSERT_NEW_DEVICE_QUERY)
                        .params(of( "userId", userId,"device", device,"client", client,"ipAddress", ipAddress )).update();
            }

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException(String.format("No user found by ID %s", userId));
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

}
