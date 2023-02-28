package com.clique.retire.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoDataMetricasDTO {

	private Integer numeroPedido;
	private Integer filial;
	private Date dataInicioIntegracao;
	private Date dataTerminoIntegracao;
	private Date dataInicioSeparacao;
	private Date dataEntrouNegociacao;
	private Date dataRecebeuUltimaTransferencia;
	private Date dataTerminoRegistro;

	private Date horarioAberturaDomingo;
	private Date horarioAberturaSegunda;
	private Date horarioAberturaTerca;
	private Date horarioAberturaQuarta;
	private Date horarioAberturaQuinta;
	private Date horarioAberturaSexta;
	private Date horarioAberturaSabado;
	private Date horarioAberturaFeriado;
	private Date horarioFechamentoDomingo;
	private Date horarioFechamentoSegunda;
	private Date horarioFechamentoTerca;
	private Date horarioFechamentoQuarta;
	private Date horarioFechamentoQuinta;
	private Date horarioFechamentoSexta;
	private Date horarioFechamentoSabado;
	private Date horarioFechamentoFeriado;
	
}