package io.selfproject.userservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.selfproject.userservice.domain.Response;
import io.selfproject.userservice.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.time.LocalTime.now;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class RequestUtils {
   public static final BiConsumer<HttpServletResponse, Response> writeResponse = (httpServletResponse, response) -> {
       try{
           var outputStream = httpServletResponse.getOutputStream();
           new ObjectMapper().writeValue(outputStream, response);
           outputStream.flush();
       }catch (Exception exception){
           throw new ApiException(exception.getMessage());
       }
   };

    public static Response handleErrorResponse(String message, String exception, HttpServletRequest request, HttpStatusCode statusCode) {
    return new Response(now().toString(),statusCode.value(), request.getRequestURI(), HttpStatus.valueOf(statusCode.value()), message,exception, emptyMap());
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception){
        if (exception instanceof AccessDeniedException){
            var apiResponse = getErrorResponse(request,response,exception, FORBIDDEN);
            writeResponse.accept(response,apiResponse);
        } else if(exception instanceof InvalidBearerTokenException){
            var apiResponse = getErrorResponse(request,response,exception, UNAUTHORIZED);
            writeResponse.accept(response,apiResponse);
        }
        else if(exception instanceof InsufficientAuthenticationException){
            var apiResponse = getErrorResponse(request,response,exception, UNAUTHORIZED);
            writeResponse.accept(response,apiResponse);
        }else if(exception instanceof MismatchedInputException){
            var apiResponse = getErrorResponse(request,response,exception, BAD_REQUEST);
            writeResponse.accept(response,apiResponse);
        }
        else if(exception instanceof DisabledException|| exception instanceof LockedException|| exception instanceof BadCredentialsException
                || exception instanceof CredentialsExpiredException || exception instanceof ApiException){
            var apiResponse = getErrorResponse(request,response,exception, BAD_REQUEST);
            writeResponse.accept(response,apiResponse);
        }else {
            var apiResponse = getErrorResponse(request,response,exception, INTERNAL_SERVER_ERROR);
            writeResponse.accept(response,apiResponse);
        }
    }

    public static Response getResponse(HttpServletRequest request, Map<?,?> data, String message, HttpStatus status) {
        return new Response(now().toString(), status.value(),request.getRequestURI(), status, message, EMPTY, data);
    }

    private static final BiFunction<Exception, HttpStatus, String> errorReason = (exception, status) -> {
        if (status.isSameCodeAs(FORBIDDEN)) {
            return String.format("You don't have the required permissions to access this resource.");
        }if (status.isSameCodeAs(UNAUTHORIZED)){
            return exception.getMessage().contains("Jwt expired at") ? "Your session has expired" : "You are not logged in.";
        }if (exception instanceof DisabledException|| exception instanceof LockedException|| exception instanceof BadCredentialsException
                || exception instanceof CredentialsExpiredException || exception instanceof ApiException){
            return exception.getMessage();
        }if (status.is5xxServerError()){
            return "An internal server error occurred. Please try again later.";
        } else {
            return "An error occurred. Please try again later.";
        }
    };

    private static Response getErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception , HttpStatus status){
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(status.value());
            return new Response(now().toString(), status.value(),request.getRequestURI(), HttpStatus.valueOf(status.value()),
                    errorReason.apply(exception,status), getRootCauseMessage(exception), Collections.EMPTY_MAP);
    }
}














