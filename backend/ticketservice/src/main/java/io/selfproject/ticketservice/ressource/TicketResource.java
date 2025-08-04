package io.selfproject.ticketservice.ressource;

import io.selfproject.ticketservice.domain.Response;
import io.selfproject.ticketservice.dtorequest.ReportRequest;
import io.selfproject.ticketservice.dtorequest.TicketRequest;
import io.selfproject.ticketservice.service.TicketService;
import io.selfproject.ticketservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static io.selfproject.ticketservice.constant.Constants.FILE_NAME_HEADER;
import static io.selfproject.ticketservice.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/ticket")
public class TicketResource {

    private final UserService userService;
    private final TicketService ticketService;

    @GetMapping("/all")
    public ResponseEntity<Response> getAllTickets(@NotNull Authentication authentication, HttpServletRequest request,
                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "100") int size,
                                                  @RequestParam(value = "status", defaultValue = "") String status,
                                                  @RequestParam(value = "type", defaultValue = "") String type,
                                                  @RequestParam(value = "filter", defaultValue = "") String filter) {
        var tickets = ticketService.getTickets(authentication.getName(), page, size, status, type, filter);
        log.debug("Found {} tickets", tickets.size());
        return ok(getResponse(request, of("tickets", tickets), "Tickets retrieved", OK));
    }

    @GetMapping("list")
    public ResponseEntity<Response> getTickets(@NotNull Authentication authentication, HttpServletRequest request,
                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                               @RequestParam(value = "size", defaultValue = "9") int size,
                                               @RequestParam(value = "status", defaultValue = "") String status,
                                               @RequestParam(value = "type", defaultValue = "") String type,
                                               @RequestParam(value = "filter", defaultValue = "") String filter) {
        var tickets = ticketService.getTickets(authentication.getName(), page, size, status, type,filter);
        var pages = ticketService.getPages(authentication.getName(), page ,size, status, type, filter);
        return ok(getResponse(request, Map.of("tickets",tickets, "pages", pages ), "Tickets retrieved" , OK));
    }

    @PostMapping("create")
    public ResponseEntity<Response> createTicket(@NotNull Authentication authentication, HttpServletRequest request, @RequestParam(value = "title", defaultValue = "") String title,
                                                 @RequestParam(value = "description", defaultValue = "") String description, @RequestParam(value = "status", defaultValue = "") String status,
                                                 @RequestParam(value = "type", defaultValue = "") String type, @RequestParam(value = "priority", defaultValue = "") String priority,
                                                 @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        var ticket = ticketService.createTicket(authentication.getName(), title, description, type, priority, files );
        return created(getUri()).body(getResponse(request, Map.of("ticket",ticket), "Tickets created successfully" , CREATED));
    }

    @GetMapping("{ticketUuid}")
    public ResponseEntity<Response> getAllTicket (@NotNull Authentication authentication, HttpServletRequest request, @PathVariable(value = "ticketUuid") String ticketUuid) {
        var ticket = ticketService.getUserTicket(authentication.getName(), ticketUuid );
        var comments = ticketService.getTicketComments(ticketUuid);
        var files = ticketService.getTicketFiles(ticketUuid);
        var tasks = ticketService.getTicketTasks(ticketUuid);
        var assignee = userService.getAssignee(ticketUuid);
        var techSupport = userService.getTechSupports();
        var user = userService.getTicketUser(ticketUuid);
        return ok(getResponse(request, Map.of("ticket",ticket,"comments", comments,"files", files,"tasks", tasks,"assignee", assignee,"techSupport", techSupport,"user", user), "Ticket retrieved" , OK));
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateTicket(@NotNull Authentication authentication, HttpServletRequest request, @RequestBody TicketRequest ticketRequest) {
        var ticket = ticketService.updateTicket(authentication.getName(), ticketRequest.getTicketUuid(), ticketRequest.getTitle(), ticketRequest.getDescription(), ticketRequest.getProgress(), ticketRequest.getType(), ticketRequest.getPriority(), ticketRequest.getStatus(), ticketRequest.getDueDate());
        var tickets = ticketService.getTickets(authentication.getName(), 0, 12, "", "", "");
        return ok(getResponse(request, of("ticket", ticket, "tickets", tickets), "Ticket updated", OK));
    }

    @PutMapping("/update/assignee")
    public ResponseEntity<Response> updateAssignee(@NotNull Authentication authentication, HttpServletRequest request,
                                                   @RequestParam(value = "userUuid", defaultValue = "") String assigneeUuid,
                                                   @RequestParam(value = "ticketUuid", defaultValue = "") String ticketUuid) {
        var assignee = userService.updateAssignee(authentication.getName(), assigneeUuid, ticketUuid);
        return ok(getResponse(request, Map.of("assignee",assignee), "Ticket assigned successfully" , OK));
    }

    @PostMapping("/comment/create")
    public ResponseEntity<Response> createComment(@NotNull Authentication authentication, HttpServletRequest request,
                                                  @RequestParam(value = "ticketUuid", defaultValue = "") String ticketUuid,
                                                  @RequestParam(value = "comment", defaultValue = "") String comment) {
        var newComment = ticketService.createComment(authentication.getName(), ticketUuid, comment);
        return created(getUri()).body(getResponse(request, Map.of("comment",newComment), "Comment created successfully" , CREATED));
    }

    @PutMapping("/comment/update")
    public ResponseEntity<Response> updateComment(@NotNull Authentication authentication, HttpServletRequest request,
                                                  @RequestParam(value = "commentUuid", defaultValue = "") String commentUuid,
                                                  @RequestParam(value = "comment", defaultValue = "") String comment) {
        var updatedComment = ticketService.updateComment(authentication.getName(), commentUuid, comment);
        return ok(getResponse(request, Map.of("comment",updatedComment), "Comment updated successfully" , OK));
    }

    @DeleteMapping("/comment/delete")
    public ResponseEntity<Response> updateComment(@NotNull Authentication authentication, HttpServletRequest request, @RequestParam(value = "commentUuid", defaultValue = "") String commentUuid) {
        ticketService.deleteComment(authentication.getName(), commentUuid);
        return ok(getResponse(request, emptyMap(), "Comment deleted successfully" , OK));
    }

    @PutMapping("/task/create")
    public ResponseEntity<Response> createTask(@NotNull Authentication authentication, HttpServletRequest request,
                                               @RequestParam(value = "ticketUuid", defaultValue = "") String ticketUuid,
                                               @RequestParam(value = "name", defaultValue = "") String name,
                                               @RequestParam(value = "description", defaultValue = "") String description,
                                               @RequestParam(value = "status", defaultValue = "") String status) {
        var task = ticketService.createTask(authentication.getName(), ticketUuid, name, description, status);
        return ok(getResponse(request, Map.of("task",task), "task created successfully" , OK));
    }

    // homework - add functionalities : updateTask and deleteTask similar to deleteTicket(archive)

    @PostMapping("/file/upload")
    public ResponseEntity<Response> uploadFiles(@NotNull Authentication authentication, HttpServletRequest request,
                                                @RequestParam(value = "ticketUuid", defaultValue = "") String ticketUuid,
                                                @RequestParam(value = "files") List<MultipartFile> files) {
        var newFiles = ticketService.uploadFile(authentication.getName(), ticketUuid, files);
        return created(getUri()).body(getResponse(request, of("files", newFiles), "Files uploaded successfully", OK));
    }

    @DeleteMapping("/file/delete")
    public ResponseEntity<Response> deleteFile(@NotNull Authentication authentication, HttpServletRequest request,@RequestParam(value = "fileUuid", defaultValue = "") String fileUuid) {
        ticketService.deleteFile(authentication.getName(), fileUuid);
        return ok(getResponse(request, emptyMap(), "Files deleted successfully" , OK));
    }

    @GetMapping("/file/download/{fileUuid}")
    public ResponseEntity<Resource> downloadFile(@NotNull Authentication authentication, HttpServletRequest request, @PathVariable("fileUuid") String fileUuid) throws IOException {
       var path = ticketService.downloadFile(authentication.getName(), fileUuid);
       var resource = new UrlResource(path.toUri());
       var headers = new HttpHeaders();
       headers.add(FILE_NAME_HEADER, resource.getFilename());
       headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;" + FILE_NAME_HEADER + "=" + resource.getFilename());
       return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(path)))
               .headers(headers).body(resource);
    }

    @PostMapping("/report")
    public ResponseEntity<Response> report(@NotNull Authentication authentication, HttpServletRequest request,@RequestBody ReportRequest report) {
        var tickets = ticketService.report(authentication.getName(), report.getFilter(), report.getFromDate(),report.getToDate(),report.getStatuses(),report.getTypes(),report.getPriorities());
        return ok(getResponse(request, Map.of("tickets", tickets), "Report returned" , OK));
    }

    @PostMapping(value = "/report/download", produces = { APPLICATION_PDF_VALUE })
    public ResponseEntity<ResponseEntity.BodyBuilder> exportPdf(@NotNull Authentication authentication, HttpServletResponse response, @RequestBody ReportRequest report) {
        ticketService.exportPdf(response, authentication.getName(), report.getFilter(), report.getFromDate(), report.getToDate(), report.getStatuses(), report.getTypes(), report.getPriorities());
        return ok().build();
    }

    private URI getUri() {
        return URI.create("/ticket/ticketUuid");
    }


}
