package com.clique.retire.dto.painel_monitoramento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReprocessamentoDTO {
	private Boolean flagReprocessar;
	private String topico; 
	private String endpoint;
	private String mensagem;
	
}
