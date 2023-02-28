package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

    Cliente findByDocumento(String documento);
}
