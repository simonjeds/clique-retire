package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clique.retire.model.drogatel.HabilitadoSap;


public interface HabilitadoSapRepository  extends JpaRepository<HabilitadoSap, Integer> {

	HabilitadoSap findByPlsTxtChave(String plsTxtChave);
	
}
