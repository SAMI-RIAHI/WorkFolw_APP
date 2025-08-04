package io.selfproject.userservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Credential {

    private Long credentialId;
    private String credentialUuid;
    private String password;
    private String createdAt;
    private String updatedAt;
}
