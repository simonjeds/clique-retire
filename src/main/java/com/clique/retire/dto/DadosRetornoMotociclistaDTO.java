package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DadosRetornoMotociclistaDTO {

  private PedidoRetornoMotociclistaDTO pedido;
  private List<MotivoDrogatelDTO> motivos;

  private String cpfMotociclista;
  private Long idMotivo;

}
