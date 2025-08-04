package io.selfproject.ticketservice.service.implementation;

import io.selfproject.ticketservice.domain.Response;
import io.selfproject.ticketservice.exception.ApiException;
import io.selfproject.ticketservice.handler.RestClientInterceptor;
import io.selfproject.ticketservice.model.User;
import io.selfproject.ticketservice.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

import static io.selfproject.ticketservice.utils.RequestUtils.convertResponse;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final RestClient restClient;

    public UserServiceImpl() {
        this.restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://localhost:8085")
                //.defaultUriVariables(Map.of("some variable", "some value"))
                //.defaultHeader("AUTHORISATION", "BEARER some value")
                .requestInterceptor(new RestClientInterceptor())
                // .requestInitializer(null)
                .build(); /* TO COMMUNICATE WITH USER-SERVICE-API WE NEED TO SEND THE TOKEN IN THE REQUEST*/
    }

    @Override
    public User getUserByUuid(String userUuid) {
        try {
            var response = restClient.get().uri("/user/profile").retrieve().body(Response.class);
            return convertResponse(response, User.class, "user");
        } catch (AccessDeniedException exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public User getTicketUser(String ticketUuid) {
        try {
            var response = restClient.get().uri("/user/assignee/" + ticketUuid).retrieve().body(Response.class);
            return convertResponse(response, User.class, "user");
        } catch (AccessDeniedException exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public User getAssignee(String ticketUuid) {
        try {
            var response = restClient.get().uri("/user/assignee/" + ticketUuid).retrieve().body(Response.class);
            return convertResponse(response, User.class, "user");
        } catch (AccessDeniedException exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public List<User> getTechSupports() {
        return List.of();
    }

    @Override
    public User updateAssignee(String userUuid, String assigneeUuid, String ticketUuid) {
        try {
            var response = restClient.put().uri("/user/assignee/update" + ticketUuid).retrieve().body(Response.class);
            return convertResponse(response, User.class, "user");
        } catch (AccessDeniedException exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

}
