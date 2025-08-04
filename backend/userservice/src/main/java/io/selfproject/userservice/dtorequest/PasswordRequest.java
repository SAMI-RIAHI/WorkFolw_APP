package io.selfproject.userservice.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordRequest {
    @NotEmpty(message = "password cannot be empty or null")
    private String currentPassword;
    @NotEmpty(message = "new password cannot be empty or null")
    private String newPassword;
    @NotEmpty(message = "confirm password be empty or null")
    private String confirmPassword;
}
