package com.clique.retire.enums;

import lombok.Getter;

import java.util.Arrays;

public enum TipoOcorrenciaEnum {

  MANUTENCAO("M"),
  FALHA_TECNICA("FT");

  @Getter
  private final String chave;

  TipoOcorrenciaEnum(String chave) {
    this.chave = chave;
  }

  public static TipoOcorrenciaEnum buscarPorChave(String chave) {
    return Arrays.stream(values())
      .filter(tipoOcorrenciaEnum -> tipoOcorrenciaEnum.getChave().equalsIgnoreCase(chave))
      .findFirst()
      .orElse(null);
  }

}
