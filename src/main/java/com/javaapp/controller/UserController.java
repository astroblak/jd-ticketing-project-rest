package com.javaapp.controller;

import com.javaapp.annotation.DefaultExceptionMessage;
import com.javaapp.dto.MailDTO;
import com.javaapp.dto.UserDTO;
import com.javaapp.entity.ConfirmationToken;
import com.javaapp.entity.ResponseWrapper;
import com.javaapp.entity.User;
import com.javaapp.exception.TicketingProjectException;
import com.javaapp.mapper.MapperUtil;
import com.javaapp.service.ConfirmationTokenService;
import com.javaapp.service.RoleService;
import com.javaapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller",description = "User API")
public class UserController {

    @Value("${app.local-url}")
    private String BASE_URL;


    private UserService userService;
    private MapperUtil mapperUtil;
    private RoleService roleService;
    private ConfirmationTokenService confirmationTokenService;

    public UserController(UserService userService, MapperUtil mapperUtil, RoleService roleService, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.roleService = roleService;
        this.confirmationTokenService = confirmationTokenService;
    }

    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PostMapping("/create-user")
    @Operation(summary = "Create new account")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketingProjectException {
        UserDTO createdUser = userService.save(userDTO);

        sendEmail(createEmail(createdUser));

        return ResponseEntity.ok(new ResponseWrapper("User has been created!", createdUser));

    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all users")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper>realAll(){
        //business logic - data
        //bind it to API

        List<UserDTO> result = userService.listAllUsers();
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved users",result));
    }

    @GetMapping("/{username}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read by username")
    // only admin should see other profiles or current user can see his profile
    public ResponseEntity<ResponseWrapper> readByUsername(@PathVariable("username")String username){
        UserDTO user = userService.findByUserName(username);
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved user",user));
    }

    @PutMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update User")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO user) throws TicketingProjectException {
        UserDTO updatedUser = userService.update(user);
        return ResponseEntity.ok(new ResponseWrapper("Successfully updated",updatedUser));
    }

    @DeleteMapping("/{username}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Delete User")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username")String username) throws TicketingProjectException {
        userService.delete(username);
        return ResponseEntity.ok(new ResponseWrapper("Successfully deleted"));
    }


    @DeleteMapping("/role")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read by role")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> readByRole(@RequestParam String role){
        List<UserDTO> userList = userService.listAllByRole(role);
        return ResponseEntity.ok(new ResponseWrapper("Successfully read users by role",userList));
    }



    private MailDTO createEmail(UserDTO userDTO){
        User user = mapperUtil.convert(userDTO, new User());

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationToken.setIsDeleted(false);

        ConfirmationToken createdConfirmationToken = confirmationTokenService.save(confirmationToken);

        return MailDTO
                .builder()
                .emailTo(user.getUserName())
                .token(createdConfirmationToken.getToken())
                .subject("Confirm Registration")
                .message("To confirm your account, please click here:")
                .url(BASE_URL + "/confirmation?token=")
                .build();
    }

    private void sendEmail(MailDTO mailDTO){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailDTO.getEmailTo());
        mailMessage.setSubject(mailMessage.getSubject());
        mailMessage.setText(mailDTO.getMessage() + mailDTO.getUrl() + mailDTO.getToken());

        confirmationTokenService.sendEmail(mailMessage);
    }


}
