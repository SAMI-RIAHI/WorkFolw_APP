package io.selfproject.ticketservice.service;

import io.selfproject.ticketservice.model.User;

import java.util.List;

public interface UserService {
    User getUserByUuid(String userUuid);
    User getTicketUser(String ticketUuid);
    User getAssignee(String ticketUuid);
    List<User> getTechSupports();
    User updateAssignee(String userUuid, String assigneeUuid, String ticketUuid);

}
