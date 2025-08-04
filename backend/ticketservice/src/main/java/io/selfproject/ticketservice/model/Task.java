package io.selfproject.ticketservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private Long taskId;
    private String taskUuid;
    private String name;
    private String description;
    private String status;
    private String firstname;
    private String lastname;
    private String imageUrl;
    private String dueDate;
    private String createdAt;
    private String updatedAt;
}
