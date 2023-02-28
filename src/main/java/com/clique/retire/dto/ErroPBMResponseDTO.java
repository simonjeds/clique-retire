package com.clique.retire.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ErroPBMResponseDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private boolean erroPbm;
  private String mensagem;

}
