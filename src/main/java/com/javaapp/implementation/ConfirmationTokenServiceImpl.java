package com.javaapp.implementation;

import com.javaapp.entity.ConfirmationToken;
import com.javaapp.exception.TicketingProjectException;
import com.javaapp.repository.ConfirmationTokenRepository;
import com.javaapp.service.ConfirmationTokenService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private ConfirmationTokenRepository confirmationTokenRepository;
    private JavaMailSender javaMailSender;

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository, JavaMailSender javaMailSender) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ConfirmationToken save(ConfirmationToken confirmationToken) {
        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public ConfirmationToken readByToken(String token) throws TicketingProjectException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElse(null);
        if (confirmationToken==null){
            throw new TicketingProjectException("This token does not exist");
        }
        if(!confirmationToken.isTokenValid(confirmationToken.getExpireDate())){
            throw new TicketingProjectException("This token has been expired");
        }
        return null;
    }

    @Override
    public void delete(ConfirmationToken confirmationToken) {

        confirmationToken.setIsDeleted(true);
        confirmationTokenRepository.save(confirmationToken);
    }
}
