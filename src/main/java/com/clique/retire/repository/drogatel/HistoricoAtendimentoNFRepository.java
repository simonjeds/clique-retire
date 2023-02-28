package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.HistoricoAtendimentoNF;

/**
 * @author Framework
 *
 */
@Repository
public interface HistoricoAtendimentoNFRepository extends JpaRepository<HistoricoAtendimentoNF, Integer>{
}
