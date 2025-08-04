package io.getarrays.authorizationserver.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author Roland Junior Toussaint
 * @version 1.0
 * @since 4/12/2021
 */

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HandleException implements ErrorController {
}