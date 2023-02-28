package com.clique.retire.repository.drogatel;

import java.util.List;

public interface PedidoMercadoriaCustom {

	/**
	 * Cancela transferência pelo número do pedido.
	 * @param numeroPedido
	 */
	public void cancelaTransferencia(Integer numeroPedido);
	
	/**
	 * Remove as referências da transferência de pedido conforme os itens especificados.
	 * @param idsItensPedido
	 */
	public void removerReferenciaPorItensPedido(List<Integer> idsItensPedido);
}
