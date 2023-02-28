package com.clique.retire.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDocumentoClienteEnum {

  CARTEIRA_DE_IDENTIDADE("2", "CARTEIRA DE IDENTIDADE");

  private final String codigo;
  private final String descricao;

}
