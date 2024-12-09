package com.trabalho.bicicletario.controller;

import com.trabalho.bicicletario.dto.CobrancaDTO;
import com.trabalho.bicicletario.dto.EmailDTO;
import com.trabalho.bicicletario.dto.NovaCobrancaDTO;
import com.trabalho.bicicletario.model.Cobranca;
import com.trabalho.bicicletario.repository.CartaoDeCreditoRepository;
import com.trabalho.bicicletario.repository.CobrancaRepository;
import com.trabalho.bicicletario.service.CobrancaService;
import com.trabalho.bicicletario.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/externo")
public class ExternoController {
    private final EmailService emailService;
    private final CobrancaService cobrancaService;

    public ExternoController(EmailService emailService, CobrancaService cobrancaService, CobrancaRepository cobrancaRepository, CartaoDeCreditoRepository cartaoDeCreditoRepository) {
        this.emailService = emailService;
        this.cobrancaService = cobrancaService;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<Void> enviarEmail(@RequestBody EmailDTO novoEmail) {
        emailService.enviarEmail(novoEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cobranca")
    public ResponseEntity<CobrancaDTO> cobranca(@RequestBody NovaCobrancaDTO novaCobrancaDTO) {
        Cobranca cobranca = cobrancaService.cobranca(novaCobrancaDTO);
        CobrancaDTO dto = new CobrancaDTO();
        dto.setCiclista(cobranca.getCiclista());
        dto.setValor(cobranca.getValor());
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
    public ResponseEntity<List<Cobranca>> filaCobranca(){
        List<Cobranca> cobranca = cobrancaService.filaCobranca();
        return ResponseEntity.ok(cobranca);
    }

    @PostMapping("/processaCobrancasEmFila")
    public ResponseEntity<List<Cobranca>> processaCobrancasEmFila() {
        List<Cobranca> cobrancas = cobrancaService.processaCobrancasEmFila();
        return ResponseEntity.ok(cobrancas);
    }

    /*
    @PostMapping("/validaCartaoDeCredito")
    public ResponseEntity<CartaoDeCreditoDTO> validaCartaoDeCredito(@RequestBody CartaoDeCreditoDTO cartaoDeCreditoDTO) {

    }
    */
}
