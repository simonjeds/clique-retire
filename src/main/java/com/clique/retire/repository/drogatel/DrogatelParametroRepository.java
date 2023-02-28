package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.DrogatelParametro;

@Repository
public interface DrogatelParametroRepository extends JpaRepository<DrogatelParametro, Integer> {

	/**
	 * Método para consulta na tabela DROGATEL_INI pelo nome do parametro .
	 * 
	 * @param nome do parametro
	 * @return DrogatelParametro
	 */
	DrogatelParametro findByNome(@Param("nome") String nome);
}
