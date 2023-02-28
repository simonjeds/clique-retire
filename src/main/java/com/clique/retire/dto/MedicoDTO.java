package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicoDTO {

  private Long id;
  private String conselho;
  private Integer numeroRegistro;
  private String ufRegistro;
  private String situacao;
  private String nome;

}
