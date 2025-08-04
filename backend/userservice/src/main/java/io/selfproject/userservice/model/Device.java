package io.selfproject.userservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private Long detailId;
    private long userId;
    private String device;
    private String client;
    private String ipAddress;
    private String createdAt;
    private String updatedAt;
}
