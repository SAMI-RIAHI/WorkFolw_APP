package io.getarrays.notificationservice.resource;

import io.getarrays.notificationservice.domain.Response;
import io.getarrays.notificationservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static io.getarrays.notificationservice.utils.RequestUtils.getResponse;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author Junior RT
 * @version 1.0
 * @license Get Arrays, LLC (<a href="https://www.getarrays.io">Get Arrays, LLC</a>)
 * @email getarrayz@gmail.com
 * @since 2/2/25
 */

@RestController
@AllArgsConstructor
@RequestMapping("/notification")
public class NotificationResource {
    private final NotificationService notificationService;

    @PostMapping("/messages")
    public ResponseEntity<Response> sendMessage(@NotNull Authentication authentication, HttpServletRequest request, @RequestParam(value = "toEmail") String toEmail, @RequestParam(value = "message") String message) {
        notificationService.sendMessage(authentication.getName(), toEmail, "Subject", message);
        var messages = notificationService.getMessages(authentication.getName());
        return created(URI.create("")).body(getResponse(request, of("messages", messages), "Message created", CREATED));
    }

    @PostMapping("/reply")
    public ResponseEntity<Response> replyToMessage(@NotNull Authentication authentication, HttpServletRequest request, @RequestParam(value = "toEmail") String toEmail, @RequestParam(value = "message") String message) {
        var newMessage = notificationService.sendMessage(authentication.getName(), toEmail, "Subject", message);
        return ok(getResponse(request, of("message", newMessage), "Message sent", OK));
    }

    @GetMapping("/messages")
    public ResponseEntity<Response> getMessages(@NotNull Authentication authentication, HttpServletRequest request) {
        var messages = notificationService.getMessages(authentication.getName());
        return ok(getResponse(request, of("messages", messages), "Message retrieved", OK));
    }

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<Response> getConversations(@NotNull Authentication authentication, HttpServletRequest request, @PathVariable String conversationId) {
        var conversation = notificationService.getConversations(authentication.getName(), conversationId);
        var messages = notificationService.getMessages(authentication.getName());
        return ok(getResponse(request, of("conversation", conversation, "messages", messages), "Message retrieved", OK));
    }

}