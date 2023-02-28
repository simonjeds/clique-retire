package com.clique.retire.dto;

import lombok.Data;

@Data
public class DadosMotociclistaDTO {

  private DataDTO data;

  @Data
  public static class DataDTO {
    private String nome;
    private String celular;
    private String cpf;
  }

}
