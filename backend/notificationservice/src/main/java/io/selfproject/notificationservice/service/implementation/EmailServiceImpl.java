package io.selfproject.notificationservice.service.implementation;

import io.selfproject.notificationservice.exception.ApiException;
import io.selfproject.notificationservice.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.Map;

import static io.selfproject.notificationservice.utils.NotificationUtils.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    // Watch this video to know how to send emails using Spring Boot: https://youtu.be/onCzCxDyR24?si=pjtZeysRH4I723Zr
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String NEW_TICKET_REQUEST = "New Support Ticket";
    public static final String PASSWORD_RESET_REQUEST = "Password Reset Request";
    public static final String ACCOUNT_VERIFICATION_TEMPLATE = "newaccount";
    public static final String PASSWORD_RESET_TEMPLATE = "resetpassword";
    public static final String NEW_TICKET_TEMPLATE = "newticket";
    public static final String NEW_COMMENT_TEMPLATE = "newcomment";
    public static final String NEW_FILE_TEMPLATE = "newfile";
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendNewAccountHtmlEmail(String name, String to, String token) {
        Map<String, Object> variables = Map.of("name", name,"url", getVerificationUrl(host, token));
        sendEmail(to, NEW_USER_ACCOUNT_VERIFICATION, ACCOUNT_VERIFICATION_TEMPLATE, variables);
    }

    @Override
    @Async
    public void sendPasswordResetHtmlEmail(String name, String to, String token) {
        Map<String, Object> variables = Map.of("name", name,"url", getResetPasswordUrl(host, token));
        sendEmail(to, PASSWORD_RESET_REQUEST, PASSWORD_RESET_TEMPLATE, variables);
    }

    @Override
    @Async
    public void sendNewTicketHtmlEmail(String name, String email, String ticketTitle, String ticketNumber, String priority) {
        String ticketNum = "Ticket #" + ticketNumber.toUpperCase().split("-")[4];
        Map<String, Object> variables = Map.of(
                "name", name,
                "priority", priority,
                "ticketTitle", ticketTitle,
                "ticketNumber", ticketNum,
                "date", new Date(),
                "url", getTicketUrl(host, ticketNumber)
        );
        sendEmail(email, NEW_TICKET_REQUEST, NEW_TICKET_TEMPLATE, variables);
    }

    @Override
    @Async
    public void sendNewFilesHtmlEmail(String name, String email, String files, String ticketTitle, String ticketNumber, String priority, String date) {
        String ticketNum = "Ticket #" + ticketNumber.toUpperCase().split("-")[4];
        Map<String, Object> variables = Map.of(
                "name", name,
                "priority", priority,
                "files", files.split(","),
                "ticketTitle", ticketTitle,
                "ticketNumber", ticketNum,
                "date", date,
                "url", getTicketUrl(host, ticketNumber)
        );
        sendEmail(email, "New File Uploaded on " + ticketNum, NEW_FILE_TEMPLATE, variables);
    }

    @Override
    @Async
    public void sendNewCommentHtmlEmail(String name, String email, String comment, String ticketTitle, String ticketNumber, String priority, String date) {
        String ticketNum = "Ticket #" + ticketNumber.toUpperCase().split("-")[4];
        Map<String, Object> variables = Map.of(
                "name", name,
                "priority", priority,
                "comment", comment,
                "ticketTitle", ticketTitle,
                "ticketNumber", ticketNum,
                "date", date,
                "url", getTicketUrl(host, ticketNumber)
        );
        sendEmail(email, "New Comment on " + ticketNum, NEW_COMMENT_TEMPLATE, variables);
    }

    private void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(subject);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new ApiException("Unable to send email");
        }
    }


    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }
}