package com.clique.retire.repository.drogatel;

import java.util.List;

/**
 * @author Framework
 *
 */
public interface IMGRepositoryCustom {

	List<byte[]> obterListaReceitaDigital(Long numeroPedido);
	
	boolean isContemReceitaDigital(Long numeroPedido);
}
