package com.clique.retire.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteDTO {
	
	private Integer id;
	private String nome;
	private String email;
	private String documento;
	private String celular;
	private String sexo;		
	
}
