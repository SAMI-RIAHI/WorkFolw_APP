package io.selfproject.ticketservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    private Long fileId;
    private String fileUuid;
    private String name;
    private String extension;
    private String formattedSize;
    private Long size;
    private String uri;
    private String createdAt;
    private String updatedAt;
}
