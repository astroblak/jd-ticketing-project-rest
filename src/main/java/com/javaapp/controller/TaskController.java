package com.javaapp.controller;

import com.javaapp.annotation.DefaultExceptionMessage;
import com.javaapp.dto.TaskDTO;
import com.javaapp.entity.ResponseWrapper;
import com.javaapp.enums.Status;
import com.javaapp.exception.TicketingProjectException;
import com.javaapp.service.ProjectService;
import com.javaapp.service.TaskService;
import com.javaapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read all tasks")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll(){
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved all the tasks",taskService.listAllTasks()));
    }

    @GetMapping("/project-manager")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read all tasks by project manager")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByProjectManager() throws TicketingProjectException {
        List<TaskDTO> taskList = taskService.listAllTasksByProjectManager();

        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved tasks by project manager",taskList));
    }

    @GetMapping("/{id}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, please try again!")
    @Operation(summary = "Read all tasks by id")
    @PreAuthorize("hasAnyAuthority('Manager','Employee')")
    public ResponseEntity<ResponseWrapper> readById(@PathVariable("id")Long id){
        TaskDTO currentTask = taskService.findById(id);
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved task",currentTask));
    }
}
