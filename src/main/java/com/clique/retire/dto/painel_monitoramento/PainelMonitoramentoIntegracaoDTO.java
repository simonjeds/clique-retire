package com.clique.retire.dto.painel_monitoramento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PainelMonitoramentoIntegracaoDTO {

	private IdentificacaoDTO identificacaoInformacao;
	private ReprocessamentoDTO reprocessamento;
	
	private String status;
	private String passo;
	private String criticidade;
	private String informacaoTI;
	private String informacaoNegocio;	

	private String dataHoraAlteracao;
	private String dataHoraRecebimento;
	private Integer codigoUsuarioAlteracao;

	private Boolean flagTempoMaximoResposta;	
}
