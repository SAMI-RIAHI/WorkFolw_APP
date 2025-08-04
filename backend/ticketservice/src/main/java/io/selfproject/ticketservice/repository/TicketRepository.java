package io.selfproject.ticketservice.repository;

import io.selfproject.ticketservice.model.Attachment;
import io.selfproject.ticketservice.model.Comment;
import io.selfproject.ticketservice.model.Task;
import io.selfproject.ticketservice.model.Ticket;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository {
    List<Ticket> getTickets(String userUuid, int page, int size, String status, String type, String filter);
    List<Ticket> getUserTickets(String userUuid, int page, int size, String status, String type, String filter);
    int getPages(String userUuid, int page, int size, String status, String type, String filter);
    int getUserPages(String userUuid, int page, int size, String status, String type, String filter);
    Ticket createTicket(String userUuid, String title, String description, String type, String priority);
    Ticket getUserTicket(String userUuid, String ticketUuid);
    Ticket getTicket(String userUuid, String ticketUuid);
    List<Comment> getTicketComments(String ticketUuid);
    List<Task> getTicketTasks(String ticketUuid);
    Comment createComment(String userUuid, String ticketUuid, String comment);
    List<Attachment> getTicketFiles(String ticketUuid);
    void deleteFile(String userUuid, String fileUuid);
    Comment updateComment(String userUuid, String commentUuid, String comment);
    void deleteComment(String userUuid, String commentUuid);
    Ticket updateTicket(String userUuid, String ticketUuid,String title, String description, int progress ,String type, String priority, String status, String dueDate);
    Task createTask(String userUuid, String ticketUuid, String name, String description, String status);
    List<Ticket> report(String userUuid, String filter, String fromDate, String toDate, List<String> statuses, List<String> types, List<String> priorities);
    List<Ticket> report(String filter, String fromDate, String toDate, List<String> statuses, List<String> types, List<String> priorities);
    Attachment saveTicketFile(Long ticketId, String filename, long size, String formattedSize, String extension, String uri);
    Attachment getTicketFile(String userUuid, String fileUuid);
}
