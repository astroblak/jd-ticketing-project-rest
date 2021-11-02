package com.javaapp.service;

import com.javaapp.dto.UserDTO;
import com.javaapp.entity.User;
import com.javaapp.exception.TicketingProjectException;

import java.util.List;

public interface UserService {

    List<UserDTO> listAllUsers();
    UserDTO findByUserName(String username);
    UserDTO save(UserDTO dto) throws TicketingProjectException;
    UserDTO update(UserDTO dto);
    void delete(String username) throws TicketingProjectException;

    void deleteByUserName(String username);

    List<UserDTO> listAllByRole(String role);

    Boolean checkIfUserCanBeDeleted(User user);


}
