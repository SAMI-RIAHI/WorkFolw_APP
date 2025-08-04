package io.selfproject.userservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PasswordToken {
    private Long PasswordTokenId;
    private Long userId;
    private String token;
    private String createdAt;
    private String updatedAt;
    private boolean expired;
}
