package io.selfproject.ticketservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Long commentId;
    private String commentUuid;
    private String comment;
    private String status;
    private boolean edited;
    private String userUuid;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
}
