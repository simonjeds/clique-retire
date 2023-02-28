package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ReceitaConferenciaDTO {

  private Integer codigo;
  private String numeroAutorizacao;
  private Date dataEmissao;
  private String tipo;
  private MedicoDTO prescritor;
  private PacienteDTO paciente;
  private List<ItemPedidoDTO> produtos;

  @JsonIgnore
  private ItemPedidoDTO item;

}
