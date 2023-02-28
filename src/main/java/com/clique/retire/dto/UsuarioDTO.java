package com.clique.retire.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDTO {

	 private String matricula;
	 private String nome;
	 private String token;
	 private String versao;

}
