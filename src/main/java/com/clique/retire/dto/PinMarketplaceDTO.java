package com.clique.retire.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class PinMarketplaceDTO {

  private String codigo;
  private String codigoLoja;

}
