package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.PedidoResumido;

/**
 * @author Framework
 *
 */
@Repository
public interface PedidoResumidoRepository extends JpaRepository<PedidoResumido, Long>{
	

}