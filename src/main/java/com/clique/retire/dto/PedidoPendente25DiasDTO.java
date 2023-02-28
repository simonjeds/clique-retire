package com.clique.retire.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PedidoPendente25DiasDTO {

	private Integer numeroPedido;
	private String nomeCliente;
	private String nomeUsuario;
	private String descricaoFase;
	private Date data;
}
