package com.trabalho.bicicletario.repository;

import com.trabalho.bicicletario.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Integer>{

}