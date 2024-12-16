package com.trabalho.bicicletario.service;

import com.trabalho.bicicletario.excecoes.Erros;
import com.trabalho.bicicletario.excecoes.Exceptions;
import com.trabalho.bicicletario.dto.EmailDTO;
import com.trabalho.bicicletario.model.Email;
import com.trabalho.bicicletario.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository, JavaMailSender enviarEmail) {
        this.emailRepository = emailRepository;
        this.enviarEmail = enviarEmail;
    }

    private final JavaMailSender enviarEmail;

    @Value("${spring.mail.username}")
    private String remetente;

    public Email enviarEmail(EmailDTO emailDTO){
        try {
            if (emailDTO == null || !isValidEmail(emailDTO.getEmail()))
                throw new Exceptions(Erros.EMAIL_INVALIDO);

            Email email = new Email();
            email.setId(null);
            email.setEmail(emailDTO.getEmail());
            email.setAssunto(emailDTO.getAssunto());
            email.setMensagem(emailDTO.getMensagem());

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(emailDTO.getEmail());
            mensagem.setSubject(emailDTO.getAssunto());
            mensagem.setText(emailDTO.getMensagem());

            enviarEmail.send(mensagem);
            return emailRepository.save(email);
        } catch (MailAuthenticationException e){
            throw new Exceptions(Erros.EMAIL_INEXISTENTE);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }
}
