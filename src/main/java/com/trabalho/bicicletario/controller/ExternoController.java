package com.trabalho.bicicletario.controller;

import com.trabalho.bicicletario.Exceptions.ErrosDTO;
import com.trabalho.bicicletario.Exceptions.Exception;
import com.trabalho.bicicletario.dto.*;
import com.trabalho.bicicletario.model.CartaoDeCredito;
import com.trabalho.bicicletario.model.Cobranca;
import com.trabalho.bicicletario.model.Email;
import com.trabalho.bicicletario.service.CobrancaService;
import com.trabalho.bicicletario.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/externo")
public class ExternoController {
    private final EmailService emailService;
    private final CobrancaService cobrancaService;

    public ExternoController(EmailService emailService, CobrancaService cobrancaService) {
        this.emailService = emailService;
        this.cobrancaService = cobrancaService;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<EmailDTO> enviarEmail(@RequestBody EmailDTO novoEmail) {
        try {
            Email email = emailService.enviarEmail(novoEmail);
            EmailDTO dto = new EmailDTO();
            dto.setId(email.getId());
            dto.setEmail(email.getEmail());
            dto.setAssunto(email.getAssunto());
            dto.setMensagem(email.getMensagem());
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @PostMapping("/cobranca")
    public ResponseEntity<CobrancaDTO> cobranca(@RequestBody NovaCobrancaDTO novaCobrancaDTO) {
        try {
            Cobranca cobranca = cobrancaService.cobranca(novaCobrancaDTO);
            CobrancaDTO dto = new CobrancaDTO();
            dto.setId(cobranca.getId());
            dto.setStatus(cobranca.getStatus());
            dto.setHoraSolicitacao(cobranca.getHoraSolicitacao());
            dto.setHoraFinalizacao(cobranca.getHoraFinalizacao());
            dto.setValor(cobranca.getValor());
            dto.setCiclista(cobranca.getCiclista());
            return ResponseEntity.ok(dto);
        } catch (Exception ex){
            throw ex;
        }
    }

    @GetMapping("/cobranca/{idCobranca}")
    public ResponseEntity<CobrancaDTO> cobranca(@PathVariable int idCobranca) {
        try {
            Cobranca cobranca = cobrancaService.cobranca(idCobranca);
            CobrancaDTO dto = new CobrancaDTO();
            dto.setId(cobranca.getId());
            dto.setStatus(cobranca.getStatus());
            dto.setHoraSolicitacao(cobranca.getHoraSolicitacao());
            dto.setHoraFinalizacao(cobranca.getHoraFinalizacao());
            dto.setValor(cobranca.getValor());
            dto.setCiclista(cobranca.getCiclista());
            return ResponseEntity.ok(dto);
        } catch (Exception ex){
            throw ex;
        }
    }

    @PostMapping("/filaCobranca")
    public ResponseEntity<List<Cobranca>> filaCobranca(@RequestBody NovaCobrancaDTO novaCobrancaDTO){
        try {
            List<Cobranca> cobrancas = new ArrayList<>();
            cobrancas.add(cobrancaService.filaCobranca(novaCobrancaDTO));
            return ResponseEntity.ok(cobrancas);
        } catch (Exception ex){
            throw ex;
        }
    }

    @PostMapping("/processaCobrancasEmFila")
    public ResponseEntity<List<Cobranca>> processaCobrancasEmFila() {
        try {
            List<Cobranca> cobrancas = cobrancaService.processaCobrancasEmFila();
            return ResponseEntity.ok(cobrancas);
        } catch (Exception ex){
            throw ex;
        }
    }

    @PostMapping("/validaCartaoDeCredito")
    public ResponseEntity<ErrosDTO> validaCartaoDeCredito(@RequestBody CartaoDeCredito cartaoDeCredito) {
        return cobrancaService.validaCartaoDeCredito(cartaoDeCredito);
    }
}
