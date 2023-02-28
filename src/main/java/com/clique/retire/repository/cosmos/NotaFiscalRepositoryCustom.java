package com.clique.retire.repository.cosmos;

import java.util.List;

import com.clique.retire.dto.ItemNotaFiscalDTO;
import com.clique.retire.model.drogatel.Pedido;

public interface NotaFiscalRepositoryCustom {
	
	/**
	 * Verifica se existe uma nota fisca relacionado ao número de pedido informado.
	 * @param numeroPedido
	 * @return True or False
	 */
	public boolean existeNotaFiscal(Long numeroPedido);

	void removerItensNotaPeloItemPedido(List<Integer> idsItensPedido);

	void atualizarValorNotaEItens(List<ItemNotaFiscalDTO> itens, Pedido pedido);

}
