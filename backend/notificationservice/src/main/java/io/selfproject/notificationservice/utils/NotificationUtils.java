package io.selfproject.notificationservice.utils;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Junior RT
 * @version 1.0
 * @license Get Arrays, LLC (<a href="https://www.getarrays.io">Get Arrays, LLC</a>)
 * @email getarrayz@gmail.com
 * @since 2/1/25
 */

public class NotificationUtils {

    public static Supplier<String> randomUUID = () -> UUID.randomUUID().toString();

    public static String getVerificationUrl(String host, String token) {
        return host + "/verify/account?token=" + token;
    }

    public static String getResetPasswordUrl(String host, String token) {
        return host + "/verify/password?token=" + token;
    }

    public static String getTicketUrl(String host, String ticketNumber) {
        return host + "/tickets/" + ticketNumber;
    }
}