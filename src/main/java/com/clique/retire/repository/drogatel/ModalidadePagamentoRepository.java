package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.ModalidadePagamento;
import com.clique.retire.model.drogatel.Pedido;

/**
 * @author Framework
 *
 */
@Repository
public interface ModalidadePagamentoRepository extends JpaRepository<ModalidadePagamento, Long>{
	
	ModalidadePagamento findByPedido(@Param("pedido") Pedido pedido);
	
}