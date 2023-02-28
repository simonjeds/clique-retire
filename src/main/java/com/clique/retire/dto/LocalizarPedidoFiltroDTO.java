package com.clique.retire.dto;

import lombok.Data;

@Data
public class LocalizarPedidoFiltroDTO {

  private String filtro;
  private Boolean todasLojas;
  private String fase;
  private String tipoPedido;
  private Boolean isEntrega;
  private Integer filial;
  private int pagina;

}
