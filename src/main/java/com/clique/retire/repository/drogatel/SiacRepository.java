package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clique.retire.model.drogatel.PrePedidoSiac;

public interface SiacRepository extends JpaRepository<PrePedidoSiac, Integer>, SiacRepositoryCustom {
	
}