package com.trabalho.bicicletario.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.trabalho.bicicletario.controller.ExternoController;
import com.trabalho.bicicletario.dto.NovaCobrancaDTO;
import com.trabalho.bicicletario.excecoes.Erros;
import com.trabalho.bicicletario.excecoes.Exceptions;
import com.trabalho.bicicletario.model.CartaoDeCredito;
import com.trabalho.bicicletario.model.Cobranca;
import com.trabalho.bicicletario.model.StatusCobranca;
import com.trabalho.bicicletario.repository.CobrancaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class CobrancaServiceTest {
    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private CartaoDeCredito cartaoDeCredito;

    @Mock
    private ResponseEntity response;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternoController externoController;

    private MockMvc mockMvc;

    @Spy
    @InjectMocks
    private CobrancaService cobrancaService;

    @BeforeEach
    void setUp() {
        //Inicializa os mock e faz a injeção na classe a ser testada
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(externoController).build();
    }

    // ---- TESTES DO MÉTODO: cobranca() ----
    @Test
    void cobranca_RetornarDadosValidos() {
        // Dados de entrada (mock de DTO)
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
        assertNotNull(result);
        assertEquals(StatusCobranca.PENDENTE.name(), result.getStatus());
        assertEquals(1, result.getCiclista());
        assertEquals(100.0, result.getValor());

        // Verifica que o método save foi chamado
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }

    @Test
    void cobranca_LancarExcecaoParaDadosInvalidos() {
        //Entradas inválidas
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
        // Mock de um objeto Cobranca
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
        // Dados de entrada
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
        // Dados de entrada
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
        // Mock de uma lista de cobranças
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

    /*
    // ---- TESTES DO MÉTODO: validaCartaoDeCredito() ----
    @Test
    void validaCartaoDeCredito_deveRetornarSucessoQuandoCartaoValido() throws Exception {
        // Arrange
        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setNumero("1234567890123456");
        ErrosDTO respostaEsperada = new ErrosDTO("200", "Dados atualizados");

        when(cobrancaService.validaCartaoDeCredito(any(CartaoDeCredito.class))).thenReturn(ResponseEntity.ok(respostaEsperada));

        // Act & Assert
        mockMvc.perform(post("/api/cobrancas/validar-cartao")
                        .contentType("application/json")
                        .content("{\"numero\": \"1234567890123456\"}"))
                .andExpect(status().isOk())  // Espera status 200 OK
                .andExpect(jsonPath("$.codigo").value("200"))  // Verifica o código de resposta
                .andExpect(jsonPath("$.mensagem").value("Dados atualizados"));  // Verifica a mensagem
    }

    // Teste para quando ocorrer uma falha na validação do cartão
    @Test
    void validaCartaoDeCredito_deveRetornarErroQuandoFalhaNaValidacao() throws Exception {
        // Arrange
        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setNumero("1234567890123456");

        // Quando ocorrer uma exceção no serviço de validação do cartão, retorna erro
        when(cobrancaService.validaCartaoDeCredito(any(CartaoDeCredito.class)))
                .thenThrow(new Exceptions(Erros.DADOS_INVALIDOS));

        // Act & Assert
        mockMvc.perform(post("/api/cobrancas/validar-cartao")
                        .contentType(MediaType.valueOf("application/json"))
                        .content("{\"numero\": \"1234567890123456\"}"))
                .andExpect(status().isBadRequest())  // Espera status 400 BAD REQUEST
                .andExpect(jsonPath("$.codigo").value("400"))  // Verifica o código de erro
                .andExpect(jsonPath("$.mensagem").value("DADOS_INVALIDOS"));  // Verifica a mensagem de erro
    }*/
}
