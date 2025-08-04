package io.selfproject.ticketservice.repository.implementation;

import io.selfproject.ticketservice.exception.ApiException;
import io.selfproject.ticketservice.model.*;
import io.selfproject.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static io.selfproject.ticketservice.query.TicketQuery.*;
import static io.selfproject.ticketservice.utils.QueryUtils.*;
import static io.selfproject.ticketservice.utils.TicketUtils.randomUUID;
import static java.util.Map.of;

@Slf4j
@Service
@RequiredArgsConstructor
public  class TicketRepositoryImpl implements TicketRepository {

    private final JdbcClient jdbcClient;

    @Override
    public List<Ticket> getTickets(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            var query = createSelectTicketsQuery(status, type, filter);
            return jdbcClient.sql(query).params(Map.of("size", size, "status", status, "type", type ,"filter", filter ,"offset" , offSet.apply(size, page))).query(Ticket.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No User found by UUID %s", userUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public List<Ticket> getUserTickets(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            var query = createSelectUserTicketsQuery(status, type, filter);
            return jdbcClient.sql(query).params(Map.of("userUuid", userUuid,"size", size, "status", status, "type", type ,"filter", filter ,"offset" , offSet.apply(size, page))).query(Ticket.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No User found by UUID %s", userUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public int getPages(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            var query = createSelectPagesQuery(status, type, filter);
            return jdbcClient.sql(query).params(Map.of("size", size, "status", status, "type", type ,"filter", filter)).query(Integer.class).single();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public int getUserPages(String userUuid, int page, int size, String status, String type, String filter) {
        try {
            var query = createSelectUserPagesQuery(status, type, filter);
            return jdbcClient.sql(query).params(Map.of("userUuid", userUuid, "size", size, "status", status, "type", type ,"filter", filter)).query(Integer.class).single();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public Ticket createTicket(String userUuid, String title, String description, String type, String priority) {
        try {
            return jdbcClient.sql(CREATE_TICKET_FUNCTION).params(of("ticketUuid", randomUUID.get(), "userUuid", userUuid, "title", title, "description", description, "type", type, "priority", priority)).query(Ticket.class).single();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Ticket getTicket(String userUuid, String ticketUuid) {
        try {
            return jdbcClient.sql(SELECT_TICKET_QUERY).params(Map.of("userUuid", userUuid, "ticketUuid", ticketUuid)).query(Ticket.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No Ticket found by user UUID %s", userUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public Ticket getUserTicket(String userUuid, String ticketUuid) {
        try {
            return jdbcClient.sql(SELECT_TICKET_BY_USER_UUID_QUERY).params(Map.of("userUuid", userUuid, "ticketUuid", ticketUuid)).query(Ticket.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No Ticket found by user UUID %s", userUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }


    @Override
    public List<Comment> getTicketComments(String ticketUuid) {
        try {
            return jdbcClient.sql(SELECT_COMMENTS_QUERY).params(Map.of("ticketUuid", ticketUuid)).query(Comment.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No Comments found for ticket UUID %s", ticketUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public List<Task> getTicketTasks(String ticketUuid) {
        try {
            return jdbcClient.sql(SELECT_TICKET_TASKS_QUERY).params(Map.of("ticketUuid", ticketUuid)).query(Task.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No Tasks found for ticket UUID %s", ticketUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public Comment createComment(String userUuid, String ticketUuid, String comment) {
        try {
            return jdbcClient.sql(CREATE_COMMENT_FUNCTION).params(Map.of("commentUuid", randomUUID.get(), "userUuid", userUuid, "ticketUuid", ticketUuid, "comment", comment)).query(Comment.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No ticket UUID %s", ticketUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public List<Attachment> getTicketFiles(String ticketUuid) {
        try {
            return jdbcClient.sql(SELECT_FILES_QUERY).params(Map.of("ticketUuid", ticketUuid)).query(Attachment.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No Files found by ticket UUID %s", ticketUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public void deleteFile(String userUuid, String fileUuid) {
        try {
            jdbcClient.sql(DELETE_FILE_QUERY).params(of("userUuid", userUuid, "fileUuid", fileUuid)).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No files found by ticket UUID %s", fileUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Comment updateComment(String userUuid, String commentUuid, String comment) {
    try {
       return jdbcClient.sql(UPDATE_COMMENT_FUNCTION).params(Map.of("commentUuid", commentUuid, "comment", comment)).query(Comment.class).single();
    } catch (EmptyResultDataAccessException exception) {
        log.error(exception.getMessage());
        throw new ApiException(String.format("No Comment found by comment UUID %s", commentUuid));
    } catch (Exception exception) {
        log.error(exception.getMessage());
        throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public void deleteComment(String userUuid, String commentUuid) {
        try {
            jdbcClient.sql(DELETE_COMMENT_QUERY).params(Map.of("userUuid", userUuid, "commentUuid", commentUuid)).update();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No Comment found by comment UUID %s", commentUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public Ticket updateTicket(String userUuid, String ticketUuid, String title, String description, int progress, String type, String priority, String status, String dueDate) {
        try {
            return jdbcClient.sql(UPDATE_TICKET_FUNCTION).params(of( "userUuid", userUuid, "ticketUuid", ticketUuid, "title", title, "description", description, "progress", progress, "type", type, "priority", priority, "status", status, "dueDate", dueDate)).query(Ticket.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No ticket found by UUID %s", ticketUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Task createTask(String userUuid, String ticketUuid, String name, String description, String status) {
        try {
            return jdbcClient.sql(CREATE_TASK_FUNCTION).params(Map.of("userUuid", userUuid, "ticketUuid", ticketUuid, "taskUuid", randomUUID.get(),  "name", name, "description", description,"status",status)).query(Task.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No ticket found by UUID %s", ticketUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }


    @Override
    public List<Ticket> report(String userUuid, String filter, String fromDate, String toDate, List<String> statuses, List<String> types, List<String> priorities) {
        try {
            var query = createTicketReportQuery(userUuid, filter, fromDate, toDate, statuses, types, priorities);
            return jdbcClient.sql(query).query(Ticket.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No user found by UUID %s", userUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public List<Ticket> report(String filter, String fromDate, String toDate, List<String> statuses, List<String> types, List<String> priorities) {
        try {
            var query = createTicketReportQuery(filter, fromDate, toDate, statuses, types, priorities);
            return jdbcClient.sql(query).query(Ticket.class).list();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No tickets found"));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public Attachment saveTicketFile(Long ticketId, String filename, long size, String formattedSize, String extension, String uri) {
        try {
            return jdbcClient.sql(SAVE_TICKET_FILE_FUNCTION).params(Map.of("fileUuid", randomUUID.get(), "ticketId", ticketId, "filename", filename, "size", size, "formattedSize", formattedSize, "extension", extension, "uri", uri)).query(Attachment.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No file found by name %s", ticketId));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    @Override
    public Attachment getTicketFile(String userUuid, String fileUuid) {
        try {
            return jdbcClient.sql(SELECT_FILES_BY_USER_UUID_QUERY).params(Map.of("userUuid", userUuid, "fileUuid", fileUuid)).query(Attachment.class).single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException(String.format("No file found by UUID %s", fileUuid));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred, Please try again...");
        }
    }

    private final BiFunction<Integer, Integer, Integer> offSet = (size, page) -> page == 0 ? 0 : page == 1 ? size : size * page;

}

