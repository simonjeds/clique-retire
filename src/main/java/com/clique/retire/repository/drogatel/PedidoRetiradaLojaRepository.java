package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.PedidoRetiradaLoja;

@Repository
public interface PedidoRetiradaLojaRepository extends JpaRepository<PedidoRetiradaLoja, Long>{

}
