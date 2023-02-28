package com.clique.retire.repository.drogatel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PedidoServico;

@Repository
public interface PedidoServicoRepository extends JpaRepository<PedidoServico, Long> {
	
	public Optional<PedidoServico> findByPedido(Pedido pedido);

}