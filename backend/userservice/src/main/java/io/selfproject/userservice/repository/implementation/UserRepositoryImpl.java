package io.selfproject.userservice.repository.implementation;

import io.selfproject.userservice.exception.ApiException;
import io.selfproject.userservice.model.*;
import io.selfproject.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static io.selfproject.userservice.query.UserQuery.*;
import static io.selfproject.userservice.utils.UserUtils.*;
import static java.lang.String.format;
import static java.sql.Types.VARCHAR;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JdbcClient jdbc;

    @Override
    public User getUserByEmail(String email) {
        try {
            return jdbc.sql(SELECT_USER_BY_EMAIL_QUERY).param("email", email).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(format("No user found user email %s", email));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User getUserByUuid(String userUuid) {
        try {
            return jdbc.sql(SELECT_USER_BY_USER_UUID_QUERY).param("userUuid", userUuid).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(format("No user found user UUID %s", userUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User getUserById(Long userId) {
        try {
            return jdbc.sql(SELECT_USER_BY_USER_ID_QUERY).param("userId", userId).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(format("No user found user ID %s", userId));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User updateUser(String userUuid, String firstName, String lastName, String email, String phone, String bio, String address) {
        try {
            return jdbc.sql(UPDATE_USER_FUNCTION).paramSource(getParamSource(userUuid, firstName, lastName, email, phone, bio, address)).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(format("No user found user UUID %s", userUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public String createUser(String firstName, String lastName, String email, String username, String password) {
        try {
            var token = randomUUID.get();
            jdbc.sql(CREATE_USER_STORED_PROCEDURE).paramSource(getParamSource(firstName, lastName, email, username, password, token)).update();
            return token;
        } catch (DuplicateKeyException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Email/username already in use. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public AccountToken getAccountToken(String token) {
        try {
            return jdbc.sql(SELECT_ACCOUNT_TOKEN_QUERY).param("token", token).query(AccountToken.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Invalid link. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verifyPasswordToken(String token) {
        return null;
    }

    @Override
    public User enableMfa(String userUuid) {
        try {
            return jdbc.sql(ENABLE_USER_MFA_FUNCTION).paramSource(getParamSource(userUuid, qrCodeSecret.get())).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User disableMfa(String userUuid) {
        try {
            return jdbc.sql(DISABLE_USER_MFA_FUNCTION).param("userUuid", userUuid).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User toggleAccountExpired(String userUuid) {
        try {
            return jdbc.sql(TOGGLE_ACCOUNT_EXPIRED_FUNCTION).param("userUuid", userUuid).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User toggleAccountLocked(String userUuid) {
        try {
            return jdbc.sql(TOGGLE_ACCOUNT_LOCKED_FUNCTION).param("userUuid", userUuid).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User toggleAccountEnabled(String userUuid) {
        try {
            return jdbc.sql(TOGGLE_ACCOUNT_ENABLED_FUNCTION).param("userUuid", userUuid).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User toggleCredentialsExpired(String userUuid) {
        return null;
    }

    @Override
    public void updatePassword(String userUuid, String encodedPassword) {
        try {
            jdbc.sql(UPDATE_USER_PASSWORD_QUERY).params(Map.of("userUuid", userUuid, "password", encodedPassword)).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User updateRole(String userUuid, String role) {
        try {
            return jdbc.sql(UPDATE_USER_ROLE_FUNCTION).params(Map.of("userUuid", userUuid, "role", role)).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void resetPassword(String email) {

    }

    @Override
    public void doResetPassword(String userUuid, String token, String password, String confirmPassword) {

    }

    @Override
    public List<User> getUsers() {
        try {
            return jdbc.sql(SELECT_USERS_QUERY).query(User.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Users not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void deleteAccountToken(String token) {
        try {
            jdbc.sql(DELETE_ACCOUNT_TOKEN_QUERY).param("token", token).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Token not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void deletePasswordToken(String token) {
        try {
            jdbc.sql("").param("token", token).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Token not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void deletePasswordToken(Long userId) {
        try {
            jdbc.sql(DELETE_PASSWORD_TOKEN_QUERY).param("userId", userId).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Token not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User getTicketUser(String ticketUuid) {
        try {
            return jdbc.sql(SELECT_TICKET_USER_QUERY).params(Map.of("ticketUuid", ticketUuid)).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(format("No ticket found by UUID %s", ticketUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public String createPasswordToken(Long userId) {
        try {
            var token = randomUUID.get();
            jdbc.sql(CREATE_PASSWORD_TOKEN_QUERY).params(Map.of("userId", userId, "token", token)).update();
            return token;
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public List<User> getTechSupports() {
        try {
            return jdbc.sql(SELECT_TECH_SUPPORTS_QUERY).query(User.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Users not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public String getPassword(String userUuid) {
        try {
            return jdbc.sql(SELECT_USER_PASSWORD_QUERY).param("userUuid", userUuid).query(String.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void updateImageUrl(String userUuid, String imageUrl) {
        try {
            jdbc.sql(UPDATE_USER_IMAGE_URL_QUERY).params(Map.of("userUuid", userUuid, "imageUrl", imageUrl)).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public PasswordToken getPasswordToken(Long userId) {
        try {
            return jdbc.sql(SELECT_PASSWORD_TOKEN_BY_USER_ID_QUERY).params(Map.of("userId", userId)).query(PasswordToken.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.debug("No password token found for user: {}", userId);
            throw new ApiException("No password reset token found");
        } catch (Exception exception) {
            log.error("Error retrieving password token for user {}: {}", userId, exception.getMessage());
            throw new ApiException("An error occurred while retrieving password token. Please try again.");
        }
    }

    @Override
    public PasswordToken getPasswordToken(String token) {
        try {
            return jdbc.sql(SELECT_PASSWORD_TOKEN_QUERY).param("token", token).query(PasswordToken.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Invalid link. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void updateAccountSettings(Long userId) {
        try {
            jdbc.sql(UPDATE_ACCOUNT_SETTINGS_QUERY).param("userId", userId).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User getAssignee(String ticketUuid) {
        try {
            return jdbc.sql(SELECT_TICKET_ASSIGNEE_QUERY).param("ticketUuid", ticketUuid).query(User.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            log.error("Ticket is not assigned.");
            return User.builder().build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Credential getCredential(String userUuid) {
        try {
            return jdbc.sql(SELECT_USER_CREDENTIAL_QUERY).param("userUuid", userUuid).query(Credential.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Credential not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public List<Device> getDevices(String userUuid) {
        try {
            return jdbc.sql(SELECT_DEVICES_QUERY).param("userUuid", userUuid).query(Device.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    private SqlParameterSource getParamSource(String userUuid, String qrCodeSecret) {
        return new MapSqlParameterSource()
                .addValue("userUuid", userUuid, VARCHAR)
                .addValue("qrCodeSecret", qrCodeSecret, VARCHAR)
                .addValue("qrCodeImageUri", qrCodeImageUri.apply(qrCodeSecret), VARCHAR);
    }

    private SqlParameterSource getParamSource(String userUuid, String firstName, String lastName, String email, String phone, String bio, String address) {
        return new MapSqlParameterSource()
                .addValue("userUuid", userUuid, VARCHAR)
                .addValue("firstName", firstName, VARCHAR)
                .addValue("lastName", lastName, VARCHAR)
                .addValue("email", email.trim().toLowerCase(), VARCHAR)
                .addValue("phone", phone, VARCHAR)
                .addValue("address", address, VARCHAR)
                .addValue("bio", bio, VARCHAR);
    }

    private SqlParameterSource getParamSource(String firstName, String lastName, String email, String username, String password, String token) {
        return new MapSqlParameterSource()
                .addValue("userUuid", randomUUID.get(), VARCHAR)
                .addValue("firstName", firstName, VARCHAR)
                .addValue("lastName", lastName, VARCHAR)
                .addValue("email", email.trim().toLowerCase(), VARCHAR)
                .addValue("username", username.trim().toLowerCase(), VARCHAR)
                .addValue("password", password, VARCHAR)
                .addValue("token", token, VARCHAR)
                .addValue("credentialUuid", randomUUID.get(), VARCHAR)
                .addValue("memberId", memberId.get(), VARCHAR);
    }
}