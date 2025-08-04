package io.getarrays.notificationservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Junior RT
 * @version 1.0
 * @license Get Arrays, LLC (<a href="https://www.getarrays.io">Get Arrays, LLC</a>)
 * @email getarrayz@gmail.com
 * @since 2/10/25
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    private String name;
    private String email;
    private String token;
    private String ticketTitle;
    private String ticketNumber;
    private String priority;
    private String comment;
    private String date;
    private String files;
}