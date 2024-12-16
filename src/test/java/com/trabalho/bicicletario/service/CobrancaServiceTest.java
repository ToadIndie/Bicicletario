package com.trabalho.bicicletario.service;

import com.trabalho.bicicletario.dto.NovaCobrancaDTO;
import com.trabalho.bicicletario.excecoes.Erros;
import com.trabalho.bicicletario.excecoes.ErrosDTO;
import com.trabalho.bicicletario.excecoes.Exceptions;
import com.trabalho.bicicletario.model.CartaoDeCredito;
import com.trabalho.bicicletario.model.Cobranca;
import com.trabalho.bicicletario.model.StatusCobranca;
import com.trabalho.bicicletario.repository.CobrancaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaServiceTest {
    @Mock
    private CobrancaRepository cobrancaRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private CobrancaService cobrancaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---- TESTES DO MÉTODO: cobranca() ----
    @Test
    void cobranca_RetornarDadosValidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(100.0);

        // Mock do comportamento de validaDados
        doReturn(true).when(cobrancaService).validaDados(1, 100.0);

        // Mock do retorno do save no repositório
        Cobranca mockCobranca = new Cobranca();
        mockCobranca.setId(null);
        mockCobranca.setStatus(StatusCobranca.PENDENTE.name());
        mockCobranca.setHoraSolicitacao(LocalDateTime.now());
        mockCobranca.setValor(100.0);
        mockCobranca.setCiclista(1);

        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(mockCobranca);

        // Executa o método
        Cobranca result = cobrancaService.cobranca(novaCobrancaDTO);

        // Validações
        assertEquals(StatusCobranca.PENDENTE.name(), result.getStatus());
        assertEquals(1, result.getCiclista());
        assertEquals(100.0, result.getValor());

        // Verifica que o método save foi chamado
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }

    @Test
    void cobranca_LancarExcecaoParaDadosInvalidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(-50.0);

        //Comportamento do método de validação dos dados de entrada
        doReturn(false).when(cobrancaService).validaDados(1, -50.0);

        //Valida a exceção lançada
        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.cobranca(novaCobrancaDTO));

        //Verifica mensagem da exceção
        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());

        //Verifica se o save não foi chamado
        verify(cobrancaRepository, never()).save(any(Cobranca.class));
    }


    // ---- TESTES DO MÉTODO: cobranca(int id) ----
    @Test
    void cobranca_RetornarCobrancaParaIdValido() {
        Cobranca mockCobranca = new Cobranca();
        mockCobranca.setId(1);
        mockCobranca.setStatus(StatusCobranca.PENDENTE.name());

        // Configura o comportamento do mock
        when(cobrancaRepository.findById(1)).thenReturn(Optional.of(mockCobranca));

        // Chama o método a ser testado
        Cobranca result = cobrancaService.cobranca(1);

        // Valida o resultado
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(StatusCobranca.PENDENTE.name(), result.getStatus());

        // Verifica se o método findById foi chamado corretamente
        verify(cobrancaRepository, times(1)).findById(1);
    }

    @Test
    void cobranca_LancarExcecaoParaIdInvalido() {
        // Configura o comportamento do mock para retornar um Optional vazio
        when(cobrancaRepository.findById(2)).thenReturn(Optional.empty());

        // Valida que a exceção personalizada é lançada
        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.cobranca(2));

        // Verifica a mensagem da exceção
        assertEquals(Erros.NAO_ENCONTRADO.getMensagem(), exception.getMessage());

        // Verifica se o método findById foi chamado corretamente
        verify(cobrancaRepository, times(1)).findById(2);
    }


    // ---- TESTES DO MÉTODO: filaCobranca() ----
    @Test
    void filaCobranca_SalvarCobrancaQuandoDadosValidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(100.0);

        // Configuração do comportamento do mock para o método privado
        doReturn(true).when(cobrancaService).validaDados(1, 100.0);

        // Mock do comportamento do save
        Cobranca mockCobranca = new Cobranca();
        mockCobranca.setId(1);
        mockCobranca.setStatus(StatusCobranca.PENDENTE.name());
        mockCobranca.setHoraSolicitacao(LocalDateTime.now());
        mockCobranca.setValor(100.0);
        mockCobranca.setCiclista(1);

        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(mockCobranca);

        // Chama o método a ser testado
        Cobranca result = cobrancaService.filaCobranca(novaCobrancaDTO);

        // Valida o resultado
        assertNotNull(result);
        assertNull(result.getId()); // O ID deve ser nulo antes de ser salvo
        assertEquals(StatusCobranca.PENDENTE.name(), result.getStatus());
        assertEquals(1, result.getCiclista());
        assertEquals(100.0, result.getValor());

        // Verifica se o repositório foi chamado
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }

    @Test
    void filaCobranca_LancarExcecaoParaDadosInvalidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(100.0);

        // Configura o comportamento do método privado
        doReturn(false).when(cobrancaService).validaDados(1, 100.0);

        // Valida que a exceção personalizada é lançada
        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.filaCobranca(novaCobrancaDTO));

        // Verifica a mensagem da exceção
        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());

        // Verifica que o método save nunca foi chamado
        verify(cobrancaRepository, never()).save(any(Cobranca.class));
    }


    // ---- TESTES DO MÉTODO: processaCobrancaEmFila() ----
    @Test
    void processaCobrancasEmFila_deveProcessarCobrancasComSucesso() {
        Cobranca cobranca1 = new Cobranca();
        cobranca1.setId(1);
        cobranca1.setStatus(StatusCobranca.PENDENTE.name());
        cobranca1.setValor(100.0);
        cobranca1.setCiclista(1);

        Cobranca cobranca2 = new Cobranca();
        cobranca2.setId(2);
        cobranca2.setStatus(StatusCobranca.PENDENTE.name());
        cobranca2.setValor(200.0);
        cobranca2.setCiclista(2);

        List<Cobranca> mockCobrancas = Arrays.asList(cobranca1, cobranca2);

        // Configura o comportamento do mock
        when(cobrancaService.cobrancas()).thenReturn(mockCobrancas);
        when(cobrancaRepository.saveAll(mockCobrancas)).thenReturn(mockCobrancas);

        // Chama o método a ser testado
        List<Cobranca> result = cobrancaService.processaCobrancasEmFila();

        // Valida o resultado
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verifica se o status foi atualizado para "PAGA" e hora de finalização foi setada
        result.forEach(cobranca -> {
            assertEquals(StatusCobranca.PAGA.name(), cobranca.getStatus());
            assertNotNull(cobranca.getHoraFinalizacao());
        });

        // Verifica se o repositório foi chamado para salvar as cobranças
        verify(cobrancaRepository, times(1)).saveAll(mockCobrancas);
    }

    @Test
    void processaCobrancasEmFila_deveLancarExcecaoQuandoErroOcorre() {
        // Configura o comportamento do mock para lançar uma exceção
        when(cobrancaService.cobrancas()).thenThrow(new Exceptions(Erros.DADOS_INVALIDOS));

        // Valida que a exceção personalizada é lançada
        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.processaCobrancasEmFila());

        // Verifica a mensagem da exceção
        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());

        // Verifica que o método saveAll nunca foi chamado
        verify(cobrancaRepository, never()).saveAll(anyList());
    }


    // ---- TESTES DO MÉTODO: validaCartaoDeCredito() ----
    @Test
    void validaCartaoDeCredito_deveRetornarSucessoQuandoCartaoValido() throws Exception {
        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setNumero("1234567890123456");

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Sucesso", HttpStatus.OK);

        // Simula a resposta do RestTemplate
        //when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

        Mockito.when(restTemplate.getForEntity(Mockito.eq("https://binlist.io/123456"), Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>("Resposta mockada", HttpStatus.OK));

        // Verificação do fluxo do método de validação
        ResponseEntity<ErrosDTO> respostaObtida = cobrancaService.validaCartaoDeCredito(cartaoDeCredito);

        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), respostaObtida.getBody());
    }

    @Test
    void validaCartaoDeCredito_deveRetornarErroQuandoFalhaNaValidacao() throws Exception {
        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setNumero("1234567890123456");

        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.validaCartaoDeCredito(cartaoDeCredito));

        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());
    }
}