package com.trabalho.bicicletario.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.trabalho.bicicletario.dto.EmailDTO;
import com.trabalho.bicicletario.excecoes.Erros;
import com.trabalho.bicicletario.excecoes.Exceptions;
import com.trabalho.bicicletario.model.Email;
import com.trabalho.bicicletario.repository.EmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender enviarEmail;

    @Mock
    private EmailRepository emailRepository;

    private EmailDTO emailDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailDTO = new EmailDTO("test@example.com", "Assunto", "Mensagem de teste");
    }

    @Test
    void enviarEmailDeveSalvarEmailEEnviarMensagem() {
        Email emailSalvo = new Email();
        emailSalvo.setId(1);
        emailSalvo.setEmail(emailDTO.getEmail());
        emailSalvo.setAssunto(emailDTO.getAssunto());
        emailSalvo.setMensagem(emailDTO.getMensagem());

        // Mock the emailRepository to return the expected emailSalvo when save() is called
        when(emailRepository.save(any(Email.class))).thenReturn(emailSalvo);

        // Mock the JavaMailSender to do nothing when sending the email
        doNothing().when(enviarEmail).send(any(SimpleMailMessage.class));

        // Act (call the method to be tested)
        Email resultado = emailService.enviarEmail(emailDTO);

        // Assert (verify the results)
        assertNotNull(resultado); // Verify that the email is saved
        assertEquals(emailSalvo.getEmail(), resultado.getEmail()); // Verify the email fields match
        assertEquals(emailSalvo.getAssunto(), resultado.getAssunto()); // Verify the subject
        assertEquals(emailSalvo.getMensagem(), resultado.getMensagem()); // Verify the message

        // Verify that the send method was called once
        verify(enviarEmail, times(1)).send(any(SimpleMailMessage.class));
        // Verify that the save method was called once with the correct Email object
        verify(emailRepository, times(1)).save(any(Email.class));
    }

    @Test
    void enviarEmailDeveLancarExceptionQuandoEmailInvalido() {
        // Arranjo
        EmailDTO invalidEmailDTO = new EmailDTO("email-invalido", "Assunto", "Mensagem de invalido");

        // Ação & Verificação
        Exception exception = assertThrows(Exceptions.class, () -> emailService.enviarEmail(invalidEmailDTO));

        assertEquals(Erros.EMAIL_INVALIDO.getMensagem(), exception.getMessage());
    }

    @Test
    void enviarEmailDeveLancarExceptionQuandoEmailDTOForNull() {
        Exception exception = assertThrows(Exceptions.class, () -> emailService.enviarEmail(null));

        assertEquals(Erros.EMAIL_INVALIDO.getMensagem(), exception.getMessage());
    }
}

