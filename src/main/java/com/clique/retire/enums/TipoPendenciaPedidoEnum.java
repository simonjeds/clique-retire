package com.clique.retire.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPendenciaPedidoEnum {

  CARTAO_DE_CREDITO("A"),
  DEPOSITO_BANCARIO("B"),
  CONTATO_CLIENTE("C"),
  DIVERSAS("D"),
  NEGOCIACAO_PEDIDO("N");

  private final String chave;

}
