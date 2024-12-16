package com.trabalho.bicicletario.service;

import com.trabalho.bicicletario.excecoes.Erros;
import com.trabalho.bicicletario.excecoes.ErrosDTO;
import com.trabalho.bicicletario.excecoes.Exceptions;
import com.trabalho.bicicletario.dto.NovaCobrancaDTO;
import com.trabalho.bicicletario.model.CartaoDeCredito;
import com.trabalho.bicicletario.model.Cobranca;
import com.trabalho.bicicletario.model.StatusCobranca;
import com.trabalho.bicicletario.repository.CobrancaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service //@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CobrancaService {
    private final CobrancaRepository cobrancaRepository;

    @Autowired
    private RestTemplate restTemplate;

    public CobrancaService(CobrancaRepository cobrancaRepository) {
        this.cobrancaRepository = cobrancaRepository;
    }

    //Realiza cobranças
    public Cobranca cobranca(NovaCobrancaDTO novaCobrancaDTO) {
        if (!validaDados(novaCobrancaDTO.getCiclista(), novaCobrancaDTO.getValor()))
            throw new Exceptions(Erros.DADOS_INVALIDOS);

        Cobranca cobranca = new Cobranca();
        cobranca.setStatus(StatusCobranca.PENDENTE.name());
        cobranca.setHoraSolicitacao(LocalDateTime.now());
        cobranca.setValor(novaCobrancaDTO.getValor());
        cobranca.setCiclista(novaCobrancaDTO.getCiclista());
        return cobrancaRepository.save(cobranca);
    }

    //Retorna cobrança por ID
    public Cobranca cobranca(int id) {
        try {
            return cobrancaRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new Exceptions(Erros.NAO_ENCONTRADO);
        }
    }

    //adiciona cobrança na fila de cobranças
    public Cobranca filaCobranca(NovaCobrancaDTO novaCobrancaDTO) {
        if (!validaDados(novaCobrancaDTO.getCiclista(), novaCobrancaDTO.getValor()))
            throw new Exceptions(Erros.DADOS_INVALIDOS);

        Cobranca cobranca = new Cobranca();
        cobranca.setId(null);
        cobranca.setStatus(StatusCobranca.PENDENTE.name());
        cobranca.setHoraSolicitacao(LocalDateTime.now());
        cobranca.setValor(novaCobrancaDTO.getValor());
        cobranca.setCiclista(novaCobrancaDTO.getCiclista());
        cobrancaRepository.save(cobranca);
        return cobranca;
    }

    public List<Cobranca> processaCobrancasEmFila() {
        try {
            List<Cobranca> cobrancas = cobrancas();
            cobrancas.forEach(cobranca -> cobranca.setStatus(StatusCobranca.PAGA.name()));
            cobrancas.forEach(cobranca -> cobranca.setHoraFinalizacao(LocalDateTime.now()));
            return cobrancaRepository.saveAll(cobrancas);
        } catch (Exceptions ex){
            throw new Exceptions(Erros.DADOS_INVALIDOS);
        }
    }

    public ResponseEntity<ErrosDTO> validaCartaoDeCredito(CartaoDeCredito cartaoDeCredito) {
        String bin = cartaoDeCredito.getNumero().substring(0, 6);

        String url = "https://binlist.io/" + bin;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            response.getStatusCode().is2xxSuccessful();
            return ResponseEntity.ok(new ErrosDTO("200", "Dados atualizados"));

        } catch (Exception ex) {
            throw new Exceptions(Erros.DADOS_INVALIDOS);
        }
    }

    // ---- MÉTODOS AUXILIARES ----
    protected boolean validaDados(int ciclista, double valor) {
        return ciclista > 0 && valor > 0;
    }

    //Chama as cobranças pendentes do banco
    protected List<Cobranca> cobrancas() {
        List<String> status = List.of(StatusCobranca.PENDENTE.name(), StatusCobranca.FALHA.name());
        return cobrancaRepository.findByStatusIn(status);
    }
}
