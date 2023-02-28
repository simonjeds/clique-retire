package com.clique.retire.repository.drogatel;

import com.clique.retire.dto.FilialDTO;
import com.clique.retire.model.drogatel.Filial;

public interface FilialRepositoryCustom {

	/**
	 * 
	 * @param codigoFilial
	 * @return
	 */
	public String obterNomeImpressora(Integer codigoFilial);
	
	/**
	 * Recupera a quantidade de novos pedidos, isto é pedidos na fase de ATENDIDO (03)
	 * @param filial
	 * @return quantidade de pedidos na fase ATENDIDO 03.
	 */
	public Integer buscarQuantidadeNovosPedidos(Integer filial);

	/**
	 * Retorna a entidade filial pelo id.
	 * 
	 * @param codigoFilial
	 * @return Filial
	 */
	public Filial findFilialById(Integer codigoFilial);
	
	public FilialDTO findFilialByIDEtiqueta (Integer filial) ;
}
