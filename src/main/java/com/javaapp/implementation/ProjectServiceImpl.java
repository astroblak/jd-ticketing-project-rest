package com.javaapp.implementation;

import com.javaapp.dto.ProjectDTO;
import com.javaapp.dto.UserDTO;
import com.javaapp.entity.Project;
import com.javaapp.entity.User;
import com.javaapp.enums.Status;
import com.javaapp.exception.TicketingProjectException;
import com.javaapp.mapper.MapperUtil;
import com.javaapp.mapper.ProjectMapper;
import com.javaapp.mapper.UserMapper;
import com.javaapp.repository.ProjectRepository;
import com.javaapp.service.ProjectService;
import com.javaapp.service.TaskService;
import com.javaapp.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {


    private ProjectRepository projectRepository;
    private MapperUtil mapperUtil;
    private UserService userService;
    private TaskService taskService;


    public ProjectServiceImpl(ProjectRepository projectRepository, MapperUtil mapperUtil, UserService userService, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return mapperUtil.convert(project,new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll(Sort.by("projectCode"));
        return list.stream().map(obj -> mapperUtil.convert(obj,new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {
        Project foundProject = projectRepository.findByProjectCode(dto.getProjectCode());
        if(foundProject!=null){
            throw new TicketingProjectException("Project with this code already exists");
        }

        Project obj = mapperUtil.convert(dto,new Project());

        Project createdProject = projectRepository.save(obj);

        return mapperUtil.convert(createdProject, new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());

        if(project == null){
            throw new TicketingProjectException("Project does not exist");
        }

        Project convertedProject = mapperUtil.convert(dto,new Project());
        Project updatedProject = projectRepository.save(convertedProject);

        return mapperUtil.convert(updatedProject, new ProjectDTO());
    }

    @Override
    public void delete(String code) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(code);

        if(project == null){
            throw new TicketingProjectException("Project does not exist");
        }

        project.setIsDeleted(true);

        project.setProjectCode(project.getProjectCode() +  "-" + project.getId());
        projectRepository.save(project);

        taskService.deleteByProject(mapperUtil.convert(project, new ProjectDTO()));
    }

    @Override
    public void complete(String projectCode) {
        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO currentUserDTO = userService.findByUserName(username);
        User user = userMapper.convertToEntity(currentUserDTO);
        List<Project> list = projectRepository.findAllByAssignedManager(user);

        return list.stream().map(project -> {
            ProjectDTO obj = projectMapper.convertToDto(project);
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());



    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(obj ->projectMapper.convertToDto(obj)).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream()
                .map(project -> projectMapper.convertToDto(project))
                .collect(Collectors.toList());
        }
}















