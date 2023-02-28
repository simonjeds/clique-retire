package com.clique.retire.repository.drogatel;

import java.util.List;

import com.clique.retire.dto.LoteBipadoDTO;

/**
 * @author Framework
 *
 */
public interface LoteBipadoRepositoryCustom {

	List<LoteBipadoDTO> buscarLotesPorPedido(Long numeroPedido);
	boolean existeDiferencaReferenciaItemNotaFiscal(Long numeroPedido);
	void atualizarCodigoItemNotaFiscal(Long numeroPedido);
	boolean existeLoteBipadoEOuReceitaExcedenteOuFaltante(Integer numeroPedido);

}
