package com.clique.retire.repository.drogatel;

import com.clique.retire.dto.CancelamentoPedidoDTO;

import java.util.Optional;

public interface CancelamentoPedidoRepositoryCustom {

  /**
   * Busca o primeiro pedido de controlado apto para solicitação de cancelamento
   * @param codFilial codigo da filial logada
   */
  Optional<CancelamentoPedidoDTO> buscarPedidoCancelamentoPorFilial(Integer codFilial);

  /**
   * Busca a quantidade de pedidos de controlado cancelados apto para retorno
   * @param codFilial codigo da filial logada
   */
  Integer buscarQuantidadePedidoCancelamentoPorFilial(Integer codFilial);

}
