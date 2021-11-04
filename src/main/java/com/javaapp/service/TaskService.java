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

    TaskDTO save(TaskDTO dto);

    TaskDTO update(TaskDTO dto) throws TicketingProjectException;

    void delete(long id) throws TicketingProjectException;

    int totalNonCompletedTasks(String projectCode);
    int totalCompletedTasks(String projectCode);

    void deleteByProject(ProjectDTO project);

    List<TaskDTO> listAllByProject(ProjectDTO project);

    List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketingProjectException;

    List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException;

    TaskDTO updateStatus(TaskDTO dto) throws TicketingProjectException;

    //List<TaskDTO> listAllTasksByStatus(Status status);

    List<TaskDTO> readAllByEmployee(User assignedEmployee);
}
