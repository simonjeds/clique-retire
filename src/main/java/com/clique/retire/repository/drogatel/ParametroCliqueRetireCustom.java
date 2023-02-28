package com.clique.retire.repository.drogatel;

import org.springframework.stereotype.Repository;

@Repository
public interface ParametroCliqueRetireCustom{

	/**
	 * Método para consulta na tabela DRGTBLPCRPARAMETRO pelo nome do parametro .
	 * 
	 * @param nome do parametro
	 * @return ParametroCliqueRetire
	 */
	public String findByNome(String nome);
}
