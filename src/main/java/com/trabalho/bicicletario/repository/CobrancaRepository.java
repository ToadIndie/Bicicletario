package com.trabalho.bicicletario.repository;

import com.trabalho.bicicletario.model.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, Integer> {
    List<Cobranca> findByStatusIn(List<String> status);
}
