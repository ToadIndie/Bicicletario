package com.trabalho.bicicletario.service;


import com.trabalho.bicicletario.Exceptions.Erros;
import com.trabalho.bicicletario.Exceptions.ErrosDTO;
import com.trabalho.bicicletario.Exceptions.Exception;
import com.trabalho.bicicletario.dto.NovaCobrancaDTO;
import com.trabalho.bicicletario.model.CartaoDeCredito;
import com.trabalho.bicicletario.model.Cobranca;
import com.trabalho.bicicletario.model.StatusCobranca;
import com.trabalho.bicicletario.repository.CartaoDeCreditoRepository;
import com.trabalho.bicicletario.repository.CobrancaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CobrancaService {
    private final CobrancaRepository cobrancaRepository;
    private final CartaoDeCreditoRepository cartaoDeCreditoRepository;

    public CobrancaService(CobrancaRepository cobrancaRepository, CartaoDeCreditoRepository cartaoDeCreditoRepository   ) {
        this.cobrancaRepository = cobrancaRepository;
        this.cartaoDeCreditoRepository = cartaoDeCreditoRepository;
    }

    //Realiza cobranças
    public Cobranca cobranca(NovaCobrancaDTO novaCobrancaDTO) {
        try {
            if (!validaDados(novaCobrancaDTO.getCiclista(), novaCobrancaDTO.getValor()))
                throw new Exception(Erros.DADOS_INVALIDOS);

            Cobranca cobranca = new Cobranca();
            cobranca.setStatus(StatusCobranca.PENDENTE.name());
            cobranca.setHoraSolicitacao(LocalDateTime.now());
            cobranca.setValor(novaCobrancaDTO.getValor());
            cobranca.setCiclista(novaCobrancaDTO.getCiclista());
            return cobrancaRepository.save(cobranca);
        } catch (java.lang.Exception ex) {
            throw ex;
        }
    }

    //Retorna cobrança por ID
    public Cobranca cobranca(int id) {
        try {
            return cobrancaRepository.findById(id).orElseThrow();
        } catch (Exception ex) {
            throw new Exception(Erros.NAO_ENCONTRADO);
        }
    }

    //adiciona cobrança na fila de cobranças
    public Cobranca filaCobranca(NovaCobrancaDTO novaCobrancaDTO) {
        try {
            if (!validaDados(novaCobrancaDTO.getCiclista(), novaCobrancaDTO.getValor()))
                throw new Exception(Erros.DADOS_INVALIDOS);

            Cobranca cobranca = new Cobranca();
            cobranca.setStatus(StatusCobranca.PENDENTE.name());
            cobranca.setHoraSolicitacao(LocalDateTime.now());
            cobranca.setValor(novaCobrancaDTO.getValor());
            cobranca.setCiclista(novaCobrancaDTO.getCiclista());

            return cobranca;
        } catch (java.lang.Exception ex) {
            throw new Exception(Erros.DADOS_INVALIDOS);
        }
    }

    public List<Cobranca> processaCobrancasEmFila() {
        try {
            List<Cobranca> cobrancas = cobrancas();
            cobrancas.forEach(cobranca -> cobranca.setStatus(StatusCobranca.PAGA.name()));
            cobrancas.forEach(cobranca -> cobranca.setHoraFinalizacao(LocalDateTime.now()));
            return cobrancaRepository.saveAll(cobrancas);
        } catch (Exception ex){
            throw new Exception(Erros.DADOS_INVALIDOS);
        }
    }

    public ResponseEntity<ErrosDTO> validaCartaoDeCredito(CartaoDeCredito cartaoDeCredito) {
        String bin = cartaoDeCredito.getNumero().substring(0, 6);

        String url = "https://binlist.io/" + bin;

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            response.getStatusCode().is2xxSuccessful();
            return ResponseEntity.ok(new ErrosDTO("200", "Dados atualizados"));

        } catch (java.lang.Exception ex) {
            throw new Exception(Erros.DADOS_INVALIDOS);
        }
    }

    // ---- MÉTODOS AUXILIARES ----

    private boolean validaDados(int ciclista, double valor) {
        if (ciclista > 0 && valor > 0)
            return true;
        return false;
    }

    //Chama as cobranças pendentes do banco
    public List<Cobranca> cobrancas() {
        List<String> status = List.of(StatusCobranca.PENDENTE.name(), StatusCobranca.FALHA.name());
        return cobrancaRepository.findByStatusIn(status);
    }
}
