package com.clique.retire.dto;

import java.util.Date;

import com.clique.retire.enums.FasePedidoEnum;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoNotaFiscalDTO {

	private Integer codigo;
	private String codEcommerce;
	private String nomeCliente;
	private String nomeResponsavel;
	private String situacao;
	private String chaveNota;
	private String mensagem;
	private Date ultimaAtualizacao;
	private FasePedidoEnum fasePedido;

}