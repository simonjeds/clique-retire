package com.clique.retire.dto;

import com.clique.retire.model.drogatel.MotivoDrogatel;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MotivoDrogatelDTO {

  private Long id;
  private String descricao;
  private String tipo;

  public MotivoDrogatelDTO(MotivoDrogatel motivoDrogatel) {
    if (Objects.nonNull(motivoDrogatel)) {
      this.id = motivoDrogatel.getId();
      this.descricao = motivoDrogatel.getDescricao();
      this.tipo = motivoDrogatel.getTipo();
    }
  }

}
