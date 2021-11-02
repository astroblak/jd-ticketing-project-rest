package com.javaapp.controller;

import com.javaapp.annotation.DefaultExceptionMessage;
import com.javaapp.dto.MailDTO;
import com.javaapp.dto.UserDTO;
import com.javaapp.entity.ConfirmationToken;
import com.javaapp.entity.ResponseWrapper;
import com.javaapp.entity.User;
import com.javaapp.entity.common.AuthenticationRequest;
import com.javaapp.exception.TicketingProjectException;
import com.javaapp.mapper.MapperUtil;
import com.javaapp.service.ConfirmationTokenService;
import com.javaapp.service.UserService;
import com.javaapp.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication Controller",description = "Authenticate API")
public class LoginController {

	@Value("${app.local-url}")
	private String BASE_URL;

	private AuthenticationManager authenticationManager;
	private UserService userService;
	private MapperUtil mapperUtil;
	private JWTUtil jwtUtil;
	private ConfirmationTokenService confirmationTokenService;

	public LoginController(AuthenticationManager authenticationManager, UserService userService, MapperUtil mapperUtil, JWTUtil jwtUtil, ConfirmationTokenService confirmationTokenService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.mapperUtil = mapperUtil;
		this.jwtUtil = jwtUtil;
		this.confirmationTokenService = confirmationTokenService;
	}

	@PostMapping("/authenticate")
	@DefaultExceptionMessage(defaultMessage = "Bad Credentials")
	@Operation(summary = "Login to application")
	public ResponseEntity<ResponseWrapper>doLogin(@RequestBody AuthenticationRequest authenticationRequest) throws TicketingProjectException {

		String password = authenticationRequest.getPassword();
		String username = authenticationRequest.getUsername();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,password);
		authenticationManager.authenticate(authentication);

		UserDTO foundUser = userService.findByUserName(username);
		User convertedUser = mapperUtil.convert(foundUser,new User());

		if(!foundUser.isEnabled()){
			throw new TicketingProjectException("Please verify your user");
		}

		String jwtToken= jwtUtil.generateToken(convertedUser);

		return ResponseEntity.ok(new ResponseWrapper("Login Successful",jwtToken));
	}

	@DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
	@PostMapping("/create-user")
	@Operation(summary = "Create new account")
	private ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketingProjectException {
		UserDTO createdUser = userService.save(userDTO);

		sendEmail(createEmail(createdUser));

		return ResponseEntity.ok(new ResponseWrapper("User has been created!", createdUser));

	}
	@DefaultExceptionMessage(defaultMessage = "Failed to confirm email, please try again!")
	@GetMapping("/confirmation")
	@Operation(summary = "Confirm account")
	private ResponseEntity<ResponseWrapper> confirmEmail(@RequestParam("token") String token) throws TicketingProjectException {
		ConfirmationToken confirmationToken = confirmationTokenService.readByToken(token);
		UserDTO confirmUser = userService.confirm(confirmationToken.getUser());
		confirmationTokenService.delete(confirmationToken);

		return ResponseEntity.ok(new ResponseWrapper("User has been confirmed", confirmUser));
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
