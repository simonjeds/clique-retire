package com.clique.retire.enums;

import lombok.Getter;

import java.util.Arrays;

public enum SistemaEnum {

  CLIQUE_RETIRE("C&R");

  @Getter
  private final String chave;

  SistemaEnum(String chave) {
    this.chave = chave;
  }

  public static SistemaEnum buscarPorChave(String chave) {
    return Arrays.stream(values())
      .filter(sistema -> sistema.getChave().equalsIgnoreCase(chave))
      .findFirst()
      .orElse(null);
  }

}
