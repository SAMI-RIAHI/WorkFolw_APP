package io.selfproject.authorizationserver.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HandleException implements ErrorController {
}