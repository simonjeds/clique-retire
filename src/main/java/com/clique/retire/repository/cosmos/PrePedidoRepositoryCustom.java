package com.clique.retire.repository.cosmos;

/**
 * @author Framework
 *
 */
public interface PrePedidoRepositoryCustom {
	
	/**
	 * Busca o codigo do pre pedido origem
	 * @param numeroPedido numero do pedido e codigoFilial codigo filial
	 * @return String
	 */
	String buscarCodigoPrePedidoOrigem(Integer numeroPrePedido, Integer codigoFilial);
}
