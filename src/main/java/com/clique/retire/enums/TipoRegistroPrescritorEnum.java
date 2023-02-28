package com.clique.retire.enums;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum TipoRegistroPrescritorEnum {

  CRM("M"),
  CRO("O"),
  CRMV("V");

  private final String codigo;

  public static TipoRegistroPrescritorEnum getValueByCodigo(String codigo) {
    return Arrays.stream(values())
      .filter(value -> value.codigo.equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);
  }

}
