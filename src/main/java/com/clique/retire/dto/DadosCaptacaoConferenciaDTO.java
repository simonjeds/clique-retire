package com.clique.retire.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DadosCaptacaoConferenciaDTO {

  private Long numeroPedido;
  private String nomeCliente;
  private String documentoCliente;
  private String matriculaVendedorCaptacao;
  private boolean conferenciaJaRealizada;
  private List<ReceitaConferenciaDTO> receitas;
  private boolean quatroPontoZero;
  private boolean receitaDigital;

}
