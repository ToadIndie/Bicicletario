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

class EmailServiceTest {

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

        when(emailRepository.save(any(Email.class))).thenReturn(emailSalvo);
        doNothing().when(enviarEmail).send(any(SimpleMailMessage.class));

        Email resultado = emailService.enviarEmail(emailDTO);

        assertNotNull(resultado);
        assertEquals(emailSalvo.getEmail(), resultado.getEmail());
        assertEquals(emailSalvo.getAssunto(), resultado.getAssunto());
        assertEquals(emailSalvo.getMensagem(), resultado.getMensagem());
        verify(enviarEmail, times(1)).send(any(SimpleMailMessage.class));
        verify(emailRepository, times(1)).save(any(Email.class));
    }

    @Test
    void enviarEmailDeveLancarExceptionQuandoEmailInvalido() {
        EmailDTO invalidEmailDTO = new EmailDTO("email-invalido", "Assunto", "Mensagem de invalido");

        Exception exception = assertThrows(Exceptions.class, () -> emailService.enviarEmail(invalidEmailDTO));
        assertEquals(Erros.EMAIL_INVALIDO.getMensagem(), exception.getMessage());
    }

    @Test
    void enviarEmailDeveLancarExceptionQuandoEmailDTOForNull() {
        Exception exception = assertThrows(Exceptions.class, () -> emailService.enviarEmail(null));
        assertEquals(Erros.EMAIL_INVALIDO.getMensagem(), exception.getMessage());
    }
}

