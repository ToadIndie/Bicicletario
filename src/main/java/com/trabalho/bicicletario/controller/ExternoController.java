package com.trabalho.bicicletario.controller;

import com.trabalho.bicicletario.excecoes.ErrosDTO;
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
    private List<Cobranca> cobrancas = new ArrayList<>();

    public ExternoController(EmailService emailService, CobrancaService cobrancaService) {
        this.emailService = emailService;
        this.cobrancaService = cobrancaService;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<EmailDTO> enviarEmail(@RequestBody EmailDTO novoEmail) {
        Email email = emailService.enviarEmail(novoEmail);
        EmailDTO dto = new EmailDTO(novoEmail.getEmail(), novoEmail.getAssunto(), novoEmail.getMensagem());
        dto.setId(email.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/cobranca")
    public ResponseEntity<CobrancaDTO> cobranca(@RequestBody NovaCobrancaDTO novaCobrancaDTO) {
        Cobranca cobranca = cobrancaService.cobranca(novaCobrancaDTO);
        CobrancaDTO dto = new CobrancaDTO();
        dto.setId(cobranca.getId());
        dto.setStatus(cobranca.getStatus());
        dto.setHoraSolicitacao(cobranca.getHoraSolicitacao());
        dto.setHoraFinalizacao(cobranca.getHoraFinalizacao());
        dto.setValor(cobranca.getValor());
        dto.setCiclista(cobranca.getCiclista());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cobranca/{idCobranca}")
    public ResponseEntity<CobrancaDTO> cobranca(@PathVariable int idCobranca) {
        Cobranca cobranca = cobrancaService.cobranca(idCobranca);
        CobrancaDTO dto = new CobrancaDTO();
        dto.setId(cobranca.getId());
        dto.setStatus(cobranca.getStatus());
        dto.setHoraSolicitacao(cobranca.getHoraSolicitacao());
        dto.setHoraFinalizacao(cobranca.getHoraFinalizacao());
        dto.setValor(cobranca.getValor());
        dto.setCiclista(cobranca.getCiclista());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/filaCobranca")
    public ResponseEntity<List<Cobranca>> filaCobranca(@RequestBody NovaCobrancaDTO novaCobrancaDTO){
        cobrancas.add(cobrancaService.filaCobranca(novaCobrancaDTO));
        return ResponseEntity.ok(cobrancas);
    }

    @PostMapping("/processaCobrancasEmFila")
    public ResponseEntity<List<Cobranca>> processaCobrancasEmFila() {
        cobrancas = cobrancaService.processaCobrancasEmFila();
        return ResponseEntity.ok(cobrancas);
    }

    @PostMapping("/validaCartaoDeCredito")
    public ResponseEntity<ErrosDTO> validaCartaoDeCredito(@RequestBody CartaoDeCredito cartaoDeCredito) {
        return cobrancaService.validaCartaoDeCredito(cartaoDeCredito);
    }
}
