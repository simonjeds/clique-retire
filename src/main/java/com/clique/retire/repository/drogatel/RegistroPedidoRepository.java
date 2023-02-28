package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.RegistroPedido;

import java.util.List;

@Repository
public interface RegistroPedidoRepository extends JpaRepository<RegistroPedido, Integer>{

	
	/**
	 * Método para obter o RegistroPedido
	 * 
	 * @param numeroPedido
	 * @return RegistroPedido.
	 */
	 @Query("FROM RegistroPedido rp WHERE rp.pedido.numeroPedido = :numeroPedido")
	 List<RegistroPedido> findByNumeroPedido(@Param("numeroPedido") Long numeroPedido);
	
	 Integer deleteByPedidoNumeroPedido(Long numeroPedido);
}
