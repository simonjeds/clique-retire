package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRetornoMotociclistaDTO {

  private Integer numeroPedido;
  private List<ItemPedidoRetornoMotociclistaDTO> itens;

}
