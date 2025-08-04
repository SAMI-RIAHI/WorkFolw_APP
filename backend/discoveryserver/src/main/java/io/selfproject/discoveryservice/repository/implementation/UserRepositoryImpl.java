package io.selfproject.discoveryservice.repository.implementation;

import io.selfproject.discoveryservice.exception.ApiException;
import io.selfproject.discoveryservice.model.User;
import io.selfproject.discoveryservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;


import static io.selfproject.discoveryservice.query.UserQuery.*;

@Slf4j
@Service
@RequiredArgsConstructor
public  class UserRepositoryImpl implements UserRepository {

    private final JdbcClient jdbcClient;

    @Override
    public User getUserByUsername(String username) {
        try {
            return jdbcClient.sql(SELECT_USER_BY_USERNAME_QUERY).param("username", username).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with Username %s not found", username));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An Error Occurred While Fetching User, Please try again later.");
        }
    }
}
