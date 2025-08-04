package io.selfproject.userservice.event;

import io.selfproject.userservice.enumeration.EventType;
import lombok.*;

import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private EventType eventType;
    private Map<String, ?> data;
}
