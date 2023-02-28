package com.clique.retire.repository.drogatel;

public interface PendenciaPedidoRepositoryCustom {

  /**
   * Remove a pendência que foi criada pelo JOB para o pedido de controlado que não foi entregue no prazo
   * @param numeroPedido identificador do pedido
   */
  void removerPendenciaPedidoControladoNaoEntregueNoPrazo(Long numeroPedido);

}
