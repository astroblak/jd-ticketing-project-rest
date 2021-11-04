package com.javaapp.service;

import com.javaapp.dto.RoleDTO;
import com.javaapp.exception.TicketingProjectException;

import java.util.List;

public interface RoleService {

    List<RoleDTO> listAllRoles();
    RoleDTO findById(Long id) throws TicketingProjectException;
}
