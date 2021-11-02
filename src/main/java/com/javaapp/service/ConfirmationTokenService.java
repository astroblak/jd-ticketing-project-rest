package com.javaapp.service;

import com.javaapp.entity.ConfirmationToken;
import com.javaapp.exception.TicketingProjectException;
import org.springframework.mail.SimpleMailMessage;

public interface ConfirmationTokenService {

    ConfirmationToken save(ConfirmationToken confirmationToken);
    void sendEmail(SimpleMailMessage email);
    ConfirmationToken readByToken(String token) throws TicketingProjectException;
    void delete(ConfirmationToken confirmationToken);

}
