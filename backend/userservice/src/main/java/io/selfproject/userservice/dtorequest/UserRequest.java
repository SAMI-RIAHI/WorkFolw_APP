package io.selfproject.userservice.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {

    @NotEmpty(message = "Please enter your first name")
    private String firstName;
    @NotEmpty(message = "Please enter your last name")
    private String lastName;
    @NotEmpty(message = "Please enter your email address")
    @Email(message = "Please enter a valid email address")
    private String email;
    @NotEmpty(message = "Please choose a username")
    private String username;
    @NotEmpty(message = "Please create a password")
    //@Size(min = 8,max = 20, message = "Your password must be at least 6 characters long")
    private String password;
    private String bio;
    private String phone;
    private String address;
}
