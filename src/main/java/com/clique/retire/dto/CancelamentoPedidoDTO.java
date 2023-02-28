package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelamentoPedidoDTO {

  private Integer numeroPedido;
  private String nomeCliente;
  private Integer numeroNotaFiscal;

}
