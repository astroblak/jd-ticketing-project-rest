package com.javaapp.service;

import com.javaapp.dto.ProjectDTO;
import com.javaapp.dto.TaskDTO;
import com.javaapp.entity.Task;
import com.javaapp.entity.User;
import com.javaapp.enums.Status;
import com.javaapp.exception.TicketingProjectException;

import java.util.List;

public interface TaskService {

    TaskDTO findById(Long id) throws TicketingProjectException;
    List<TaskDTO> listAllTasks();
    Task save(TaskDTO dto);
    void update(TaskDTO dto);
    void delete(long id);

    int totalNonCompletedTasks(String projectCode);
    int totalCompletedTasks(String projectCode);

    void deleteByProject(ProjectDTO project);

    List<TaskDTO> listAllByProject(ProjectDTO project);

    List<TaskDTO> listAllTasksByStatusIsNot(Status status);

    List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException;

    void updateStatus(TaskDTO dto);

    List<TaskDTO> listAllTasksByStatus(Status status);

    List<TaskDTO> readAllByEmployee(User assignedEmployee);
}
