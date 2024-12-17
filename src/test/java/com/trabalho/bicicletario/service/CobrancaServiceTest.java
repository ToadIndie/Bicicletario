package com.trabalho.bicicletario.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class CobrancaServiceTest {

    @Spy
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

    @Test
    void cobranca_RetornarDadosValidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(100.0);
        Cobranca mockCobranca = new Cobranca();
        mockCobranca.setId(null);
        mockCobranca.setStatus(StatusCobranca.PENDENTE.name());
        mockCobranca.setHoraSolicitacao(LocalDateTime.now());
        mockCobranca.setValor(100.0);
        mockCobranca.setCiclista(1);

        doReturn(true).when(cobrancaService).validaDados(anyInt(), anyDouble());
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(mockCobranca);

        Cobranca result = cobrancaService.cobranca(novaCobrancaDTO);

        assertNotNull(result);
        assertEquals(StatusCobranca.PENDENTE.name(), result.getStatus());
        assertEquals(1, result.getCiclista());
        assertEquals(100.0, result.getValor());
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }
    @Test
    void cobranca_LancarExcecaoParaDadosInvalidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(-50.0);

        doReturn(false).when(cobrancaService).validaDados(anyInt(), anyDouble());
        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.cobranca(novaCobrancaDTO));
        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());
        verify(cobrancaRepository, never()).save(any(Cobranca.class));
    }
    @Test
    void cobranca_RetornarCobrancaParaIdValido() {
        Cobranca mockCobranca = new Cobranca();
        mockCobranca.setId(1);
        mockCobranca.setStatus(StatusCobranca.PENDENTE.name());

        when(cobrancaRepository.findById(anyInt())).thenReturn(Optional.of(mockCobranca));

        Cobranca result = cobrancaService.cobranca(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(StatusCobranca.PENDENTE.name(), result.getStatus());
        verify(cobrancaRepository, times(1)).findById(1);
    }
    @Test
    void cobranca_LancarExcecaoParaIdInvalido() {
        when(cobrancaRepository.findById(2)).thenReturn(Optional.empty());

        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.cobranca(2));
        assertEquals(Erros.NAO_ENCONTRADO.getMensagem(), exception.getMessage());
        verify(cobrancaRepository, times(1)).findById(2);
    }


    @Test
    void filaCobranca_SalvarCobrancaQuandoDadosValidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(100.0);
        Cobranca mockCobranca = new Cobranca();
        mockCobranca.setId(1);
        mockCobranca.setStatus(StatusCobranca.PENDENTE.name());
        mockCobranca.setHoraSolicitacao(LocalDateTime.now());
        mockCobranca.setValor(100.0);
        mockCobranca.setCiclista(1);

        when(cobrancaService.validaDados(anyInt(), anyDouble())).thenReturn(Boolean.TRUE);
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(mockCobranca);

        Cobranca result = cobrancaService.filaCobranca(novaCobrancaDTO);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(StatusCobranca.PENDENTE.name(), result.getStatus());
        assertEquals(1, result.getCiclista());
        assertEquals(100.0, result.getValor());
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }
    @Test
    void filaCobranca_LancarExcecaoParaDadosInvalidos() {
        NovaCobrancaDTO novaCobrancaDTO = new NovaCobrancaDTO();
        novaCobrancaDTO.setCiclista(1);
        novaCobrancaDTO.setValor(100.0);

        when(cobrancaService.validaDados(anyInt(), anyDouble())).thenReturn(Boolean.FALSE);
        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.filaCobranca(novaCobrancaDTO));
        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());
        verify(cobrancaRepository, never()).save(any(Cobranca.class));
    }


    @Test
    void processaCobrancasEmFila_deveProcessarCobrancasComSucesso() {
        Cobranca cobranca1 = new Cobranca();
        cobranca1.setStatus(StatusCobranca.PENDENTE.name());
        Cobranca cobranca2 = new Cobranca();
        cobranca2.setStatus(StatusCobranca.FALHA.name());
        Cobranca cobranca3 = new Cobranca();
        cobranca3.setStatus(StatusCobranca.PAGA.name());
        cobranca3.setHoraFinalizacao(LocalDateTime.now());
        List<Cobranca> mockCobrancas = new ArrayList<>();
        mockCobrancas.add(cobranca1);
        mockCobrancas.add(cobranca2);
        List<Cobranca> mockCobrancas2= new ArrayList<>();
        mockCobrancas2.add(cobranca3);
        mockCobrancas2.add(cobranca3);

        when(cobrancaService.cobrancas()).thenReturn(mockCobrancas);
        when(cobrancaRepository.saveAll(anyList())).thenReturn(mockCobrancas2);

        List<Cobranca> result = cobrancaService.processaCobrancasEmFila();
        assertNotNull(result);
        assertEquals(2, result.size());

        result.forEach(cobranca -> {
            assertEquals(StatusCobranca.PAGA.name(), cobranca.getStatus());
            assertNotNull(cobranca.getHoraFinalizacao());
        });
        verify(cobrancaRepository, times(1)).saveAll(mockCobrancas);
    }
    @Test
    void processaCobrancasEmFila_deveLancarExcecaoQuandoErroOcorre() {
        when(cobrancaService.cobrancas()).thenThrow(new Exceptions(Erros.DADOS_INVALIDOS));

        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.processaCobrancasEmFila());
        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());
        verify(cobrancaRepository, never()).saveAll(anyList());
    }


    @Test
    void validaCartaoDeCredito_deveRetornarSucessoQuandoCartaoValido() throws Exception {
        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setNumero("1234567890123456");
        ErrosDTO respostaEsperada = new ErrosDTO("200", "Dados atualizados");
        ResponseEntity response = new ResponseEntity<>("Sucesso!", HttpStatus.OK);

        when(restTemplate.getForEntity("https://binlist.io/123456", String.class)).thenReturn(response);
        assertEquals(cobrancaService.validaCartaoDeCredito(cartaoDeCredito).getBody().getMensagem(), respostaEsperada.getMensagem());
    }
    @Test
    void validaCartaoDeCredito_deveRetornarErroQuandoFalhaNaValidacao() throws Exception {
        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setNumero("1234567890123456");

        Exceptions exception = assertThrows(Exceptions.class, () -> cobrancaService.validaCartaoDeCredito(cartaoDeCredito));
        assertEquals(Erros.DADOS_INVALIDOS.getMensagem(), exception.getMessage());
    }
}
