package com.trabalho.bicicletario.excecoes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exceptions.class)
    public ResponseEntity<List<ErrosDTO>> handleCustomException(Exceptions ex) {
        List<ErrosDTO> errors = new ArrayList<>();
        errors.add(new ErrosDTO(
                String.valueOf(ex.getErro().getCodigo()),
                ex.getErro().getMensagem()
        ));
        return ResponseEntity.status(ex.getErro().getCodigo()).body(errors);
    }
}
