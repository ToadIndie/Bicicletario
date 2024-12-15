package com.trabalho.bicicletario.service;

import com.trabalho.bicicletario.Exceptions.Erros;
import com.trabalho.bicicletario.Exceptions.Exception;
import com.trabalho.bicicletario.dto.EmailDTO;
import com.trabalho.bicicletario.model.Email;
import com.trabalho.bicicletario.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Autowired
    private JavaMailSender enviarEmail;

    @Value("${spring.mail.username}")
    private String remetente;

    public Email enviarEmail(EmailDTO emailDTO){
        try {
            if (!isValidEmail(emailDTO.getEmail()))
                throw new Exception(Erros.EMAIL_INVALIDO);

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
            throw new Exception(Erros.EMAIL_INEXISTENTE);
        } catch (java.lang.Exception ex){
            throw ex;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }
}
