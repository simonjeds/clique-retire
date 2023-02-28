package com.clique.retire.enums;

import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public enum SimNaoEnum {

  S("S"), N("N");

  @Getter
  private final String descricao;

  SimNaoEnum(String descricao) {
    this.descricao = descricao;
  }

  public static SimNaoEnum getValueByBoolean(Boolean isParceiro) {
    return BooleanUtils.isTrue(isParceiro) ? S : N;
  }

  public static SimNaoEnum getValueByString(String value) {
    return "S".equalsIgnoreCase(value) ? S : N;
  }

	public static boolean getBooleanByValue(SimNaoEnum simNaoEnum) {
		return Objects.nonNull(simNaoEnum) && simNaoEnum.equals(S);
	}

    public boolean booleanValue() {
      return S.equals(this);
    }

}