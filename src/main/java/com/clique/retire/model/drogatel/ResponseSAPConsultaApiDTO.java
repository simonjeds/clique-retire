package com.clique.retire.model.drogatel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSAPConsultaApiDTO {

	private Boolean statusContigencia;
	private Boolean confirmacao;
	private String message;
	
}
