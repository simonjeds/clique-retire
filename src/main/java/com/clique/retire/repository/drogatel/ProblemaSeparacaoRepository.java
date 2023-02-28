package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.ProblemaSeparacaoPedido;

@Repository
public interface ProblemaSeparacaoRepository extends JpaRepository<ProblemaSeparacaoPedido, Integer> {
	
	 Integer deleteByPedidoNumeroPedido(Long numeroPedido);

}