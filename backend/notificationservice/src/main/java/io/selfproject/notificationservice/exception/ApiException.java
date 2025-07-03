package io.selfproject.notificationservice.exception;

/**
 * @author Junior RT
 * @version 1.0
 * @license Get Arrays, LLC (<a href="https://www.getarrays.io">Get Arrays, LLC</a>)
 * @email getarrayz@gmail.com
 * @since 1/20/25
 */
public class ApiException extends RuntimeException {
    public ApiException(String message) { super(message); }
}