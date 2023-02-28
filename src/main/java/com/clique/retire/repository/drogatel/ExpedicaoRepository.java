package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Expedicao;

@Repository
public interface ExpedicaoRepository extends JpaRepository<Expedicao, Integer>{

}
