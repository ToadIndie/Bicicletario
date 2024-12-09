package com.trabalho.bicicletario.service;

import com.trabalho.bicicletario.dto.EmailDTO;
import com.trabalho.bicicletario.model.Email;
import com.trabalho.bicicletario.repository.CartaoDeCreditoRepository;
import com.trabalho.bicicletario.repository.CobrancaRepository;
import com.trabalho.bicicletario.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
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

    public String enviarEmail(EmailDTO emailDTO){
        try {
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
            emailRepository.save(email);
            return "Email enviado com sucesso!";
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
